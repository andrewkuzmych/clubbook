//
//  ChatViewController.m
//  Clubbook
//
//  Created by Andrew on 7/7/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ChatViewController.h"
#import "Conversation.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "CSNotificationView.h"
#import "UserViewController.h"
#import "SWRevealViewController.h"
#import "LocationManagerSingleton.h"
#import "UIImage+FixOrientation.h"
#import "DateHelper.h"
#import "Convertor.h"


@interface ChatViewController (){
    bool canChat;
    Chat *_chat;
    NSString* accessToken;
}

@end

@implementation ChatViewController
{
    UIImagePickerController* picker;
    UIImage* chatShowImage;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    accessToken = [defaults objectForKey:@"accessToken"];
    self.senderId = [defaults objectForKey:@"userId"];;
    
    [PubNub setDelegate:self];
    // Do any additional setup after loading the view.
    
    [self initToolbarButtons];
    
    picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    
    _docController = nil;
    
    // chat bubbles
    JSQMessagesBubbleImageFactory* factory = [[JSQMessagesBubbleImageFactory alloc] init];
    self.companionBubble = [factory incomingMessagesBubbleImageWithColor:[UIColor jsq_messageBubbleLightGrayColor]];
    self.userBubble = [factory outgoingMessagesBubbleImageWithColor:[UIColor jsq_messageBubbleGreenColor]];
    
    [self._manager retrieveConversation:self.senderId toUser:self.userTo accessToken:accessToken];
    [self showProgress:YES title:nil];
}

- (void) initToolbarButtons {
    //input toolbar cuztomize
    UIButton* locationButton = [[UIButton alloc] init];
    UIImage* locationImage = [UIImage imageNamed:@"map"];
    [locationButton setImage:locationImage forState:UIControlStateNormal];
    [locationButton addTarget:self action:@selector(showShareMenu) forControlEvents:UIControlEventTouchUpInside];
    
    UIButton* photoButton = [[UIButton alloc] init];
    [photoButton setTitle:@"Like" forState:UIControlStateNormal];
    [photoButton addTarget:self action:@selector(sendLike) forControlEvents:UIControlEventTouchUpInside];
    
    [self.inputToolbar.contentView setLeftBarButtonItem:locationButton];
    [self.inputToolbar.contentView setMiddleBarButtonItem:photoButton];
    
    self.inputToolbar.contentView.backgroundColor = [UIColor colorWithRed:52/255.0 green:3/255.0 blue:69/255.0 alpha:1.0];
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message {
    NSDictionary *dataJson = [message.message valueForKey:@"data"];
    NSDictionary *messageJson = [dataJson valueForKey:@"last_message"];
    NSString *msg = [messageJson valueForKey:@"msg"];
    NSString *type = [messageJson valueForKey:@"type"];
    NSString *user_from = [dataJson valueForKey:@"user_from"];
    NSString *user_to = [dataJson valueForKey:@"user_to"];
    NSString *url = [messageJson valueForKey:@"url"];
    NSDictionary* location = [messageJson valueForKey:@"location"];
    
    if ([user_to isEqualToString:self.senderId] && [user_from isEqualToString:self.userTo]) {
        canChat = YES;
        [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
        [self loadMessage:user_from displayName:user_from date:[NSDate date] message:msg type:type url:url location:location];
        [self._manager readChat:_chat.currentUser.id toUser:_chat.receiver.id accessToken:accessToken];
        [self finishReceivingMessage];
    }
}

- (void)didReceiveConversation:(Chat *)chat
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        self.title = chat.receiver.name;
        _chat = chat;
        self.messages = [[NSMutableArray alloc] init];
        
        // get user to avatar
        CGFloat incomingDiameter = self.collectionView.collectionViewLayout.incomingAvatarViewSize.width;
        CGFloat outgoingDiameter = incomingDiameter;
        
        dispatch_queue_t concurrentQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        dispatch_async(concurrentQueue, ^{
            __block UIImage* userImg;
            __block UIImage* compImg;
            dispatch_sync(concurrentQueue, ^{
                CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
                CLTransformation *transformation = [CLTransformation transformation];
                [transformation setParams: @{@"width": @256, @"height": @256, @"crop": @"thumb", @"gravity": @"face"}];
                
                NSString * receiverUserUrl  = [cloudinary url: [chat.receiver.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
                NSURL *receiverUserUrlImageURL = [NSURL URLWithString:receiverUserUrl];
                NSData *receiverUserData = [NSData dataWithContentsOfURL:receiverUserUrlImageURL];
                compImg = [UIImage imageWithData:receiverUserData];
                
                NSString * senderUserUrl  = [cloudinary url: [chat.currentUser.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
                NSURL *senderUserUrlImageURL = [NSURL URLWithString:senderUserUrl];
                NSData *senderUserData = [NSData dataWithContentsOfURL:senderUserUrlImageURL];
                userImg = [UIImage imageWithData:senderUserData];
            });
            dispatch_sync(dispatch_get_main_queue(), ^{
                self.companionAvatar =  [JSQMessagesAvatarImageFactory avatarImageWithImage:compImg diameter:incomingDiameter];
                self.userAvatar =  [JSQMessagesAvatarImageFactory avatarImageWithImage:userImg diameter:outgoingDiameter];
                
                // set user to photo button
                UIImage *image =  self.companionAvatar.avatarImage;
                UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
                button.bounds = CGRectMake( 0, 0, image.size.width, image.size.height );
                [button setImage:image forState:UIControlStateNormal];
                [button addTarget:self action:@selector(onUser:) forControlEvents:UIControlEventTouchUpInside];
                UIBarButtonItem *barButtonItem = [[UIBarButtonItem alloc] initWithCustomView:button];
                self.navigationItem.rightBarButtonItem = barButtonItem;
            });
        });

        for(Conversation * conf in chat.conversations) {
            [self loadMessage:conf.user_from displayName:conf.user_from date:conf.time message:conf.msg type:conf.type url:conf.url location:conf.location];
        }
        
        [self setCanChat];
        [self finishReceivingMessage];
        
        
        [self._manager readChat:chat.currentUser.id toUser:chat.receiver.id accessToken:accessToken];
        
    });
}

-(void)onUser:(UIButton *)sender
{
    if (self.isFromUser) {
        [self.navigationController popViewControllerAnimated:YES];
    } else {
        [self performSegueWithIdentifier: @"onUser" sender: self];
    }
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(User *)sender
{
    if([[segue identifier] isEqualToString:@"onUser"]){
        UserViewController *userController =  [segue destinationViewController];
        userController.isFromChat = YES;
        userController.user = _chat.receiver;
    }
}

- (void)setCanChat
{
    if ([self.messages count] < 3) {
        canChat = true;
        return;
    }
    
    for (JSQMessage * mess in self.messages ) {
        if ([mess.senderId isEqualToString:self.userTo]) {
            canChat = true;
            return;
        }
    }
    canChat = false;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Chat Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}
#pragma mark - Actions

- (void)sendMessage:(NSString *)message url:(NSString*)url type:(NSString *)type location:(NSDictionary*)location
{
    if (!canChat) {
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"chat_limit", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    
    NSString* trimMessage = [message stringByTrimmingCharactersInSet:
                               [NSCharacterSet whitespaceCharacterSet]];
    // send to server
    [self._manager chat:self.senderId user_to:self.userTo msg:trimMessage msg_type:type url:url location:location accessToken:accessToken];
    [self setCanChat];
}

- (void)loadMessage:(NSString*) sender displayName:(NSString*)displayName date:(NSDate*)date message:(NSString*)message type:(NSString*)type url:(NSString*)url location:(NSDictionary*) location{
    if ([type isEqualToString:@"photo"]) {
        [self loadPhotoMessage:sender displayName:displayName date:date url:url];
    }
    else if ([type isEqualToString:@"location"]) {
        [self loadLocationMessage:sender displayName:displayName date:date location:location];
    }
    else {
        [self loadTextMessage:sender displayName:displayName date:date message:message];
    }
}

- (void) loadPhotoMessage:(NSString*) sender displayName:(NSString*)displayName date:(NSDate*)date url:(NSString*)url {
    JSQPhotoMediaItem *photoItem = [[JSQPhotoMediaItem alloc] initWithImage:nil andFilePath:url];
    
    __weak UICollectionView *weakView = self.collectionView;
    [photoItem setImageWithURL:url withCompletionBlock:^{
        [weakView reloadData];
    }];
    
    if([sender isEqualToString:self.senderId]) {
        [photoItem setAppliesMediaViewMaskAsOutgoing:YES];
    }
    else {
        [photoItem setAppliesMediaViewMaskAsOutgoing:NO];
    }
    JSQMessage *photoMessage = [[JSQMessage alloc] initWithSenderId:sender senderDisplayName:displayName date:date media:photoItem];
    
    
    [self.messages addObject:photoMessage];
}

- (void) uploadPhotoMessage:(UIImage*) image {
    __block JSQPhotoMediaItem *photoItem = [[JSQPhotoMediaItem alloc] initWithImage:nil andFilePath:nil];
    // Get PNG data from following method
    [photoItem setAppliesMediaViewMaskAsOutgoing:YES];
    
    JSQMessage *photoMessage = [[JSQMessage alloc] initWithSenderId:self.senderId senderDisplayName:self.senderDisplayName date:[NSDate date] media:photoItem];

    [self.messages addObject:photoMessage];
    [self finishReceivingMessage];
    [self scrollToBottomAnimated:YES];
    
    UIImage* fixedImage = [image fixOrientation];
    
    NSData *myData = UIImageJPEGRepresentation(fixedImage, 0);
    // upload image to coudinary
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLUploader* uploader = [[CLUploader alloc] init:cloudinary delegate:self];
    
    [uploader upload:myData options:@{} withCompletion:^(NSDictionary *successResult, NSString *errorResult, NSInteger code, id context) {
        if (successResult) {
            __weak UICollectionView *weakView = self.collectionView;
            NSString *photoResult = [successResult objectForKey:@"url"];
            [self sendMessage:@"Check my photo!" url:photoResult type:@"photo" location:nil];
            [photoItem setImageWithURL:photoResult withCompletionBlock:^{
                [weakView reloadData];
            }];
            
        }
    } andProgress:^(NSInteger bytesWritten, NSInteger totalBytesWritten, NSInteger totalBytesExpectedToWrite, id context) {
        NSLog(@"Block upload progress: %ld/%ld (+%ld)", (long)totalBytesWritten, (long)totalBytesExpectedToWrite, (long)bytesWritten);
    }];
    

}

- (void) loadLocationMessage:(NSString*) sender displayName:(NSString*)displayName date:(NSDate*)date location:(NSDictionary*) location {
    __weak UICollectionView *weakView = self.collectionView;
    NSNumber* lat = [location objectForKey:@"lat"];
    NSNumber* lon = [location objectForKey:@"lon"];
    
    CLLocation *userLocation = [[CLLocation alloc] initWithLatitude:[lat doubleValue] longitude:[lon doubleValue]];
    
    JSQLocationMediaItem *locationItem = [[JSQLocationMediaItem alloc] init];
    if([sender isEqualToString:self.senderId]) {
        [locationItem setAppliesMediaViewMaskAsOutgoing:YES];
    }
    else {
        [locationItem setAppliesMediaViewMaskAsOutgoing:NO];
    }
    [locationItem setLocation:userLocation withCompletionHandler:^{
        [weakView reloadData];
    }];
    
    JSQMessage *locationMessage = [[JSQMessage alloc] initWithSenderId:sender senderDisplayName:displayName date:date media:locationItem];
    [self.messages addObject:locationMessage];
}

- (void) loadTextMessage:(NSString*) sender displayName:(NSString*)displayName date:(NSDate*)date message:(NSString*)message {
    JSQMessage *jsqmessage = [[JSQMessage alloc] initWithSenderId:sender senderDisplayName:displayName date:date text:message];
    [self.messages addObject:jsqmessage];
}

#pragma mark - JSQMessagesViewController method overrides

- (void)didPressSendButton:(UIButton *)button
           withMessageText:(NSString *)text
                  senderId:(NSString *)senderId
         senderDisplayName:(NSString *)senderDisplayName
                      date:(NSDate *)date
{
    [JSQSystemSoundPlayer jsq_playMessageSentSound];
    [self sendMessage:text url:@"" type:@"message" location:nil];
    [self finishSendingMessage];
}

- (void)didPressAccessoryButton:(UIButton *)sender
{
}


#pragma mark - JSQMessages CollectionView DataSource

- (id<JSQMessageData>)collectionView:(JSQMessagesCollectionView *)collectionView messageDataForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.messages objectAtIndex:indexPath.item];
}

- (id<JSQMessageBubbleImageDataSource>)collectionView:(JSQMessagesCollectionView *)collectionView messageBubbleImageDataForItemAtIndexPath:(NSIndexPath *)indexPath {

    JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
    if([message.senderId isEqualToString:self.userTo]) {
        return self.companionBubble;
    }
    
    return self.userBubble;
}

- (id<JSQMessageAvatarImageDataSource>)collectionView:(JSQMessagesCollectionView *)collectionView avatarImageDataForItemAtIndexPath:(NSIndexPath *)indexPath;
{
    JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
    JSQMessagesAvatarImage *avatarImage = self.userAvatar;

    if([message.senderId isEqualToString:self.userTo])
       avatarImage =  self.companionAvatar;
    
    return avatarImage;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForCellTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
    if ([self previousMessageHaveDifferentDate:indexPath.item]) {
        JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
        NSString* dateOnly = [[JSQMessagesTimestampFormatter sharedFormatter] relativeDateForDate:message.date];
        return [[NSAttributedString alloc] initWithString:dateOnly];
    }
    return nil;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForMessageBubbleTopLabelAtIndexPath:(NSIndexPath *)indexPath {
     return nil;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForCellBottomLabelAtIndexPath:(NSIndexPath *)indexPath {
    JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
    NSString* timeOnly;
    
    if ([[DateHelper sharedSingleton] is24hFormat]) {
        timeOnly = [[DateHelper sharedSingleton] get24hTime:message.date];
    }
    else {
        timeOnly = [[DateHelper sharedSingleton] get12hTime:message.date];
    }

    return [[NSAttributedString alloc] initWithString:timeOnly];
}

#pragma mark - UICollectionView DataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return [self.messages count];
}

- (UICollectionViewCell *)collectionView:(JSQMessagesCollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    JSQMessagesCollectionViewCell *cell = (JSQMessagesCollectionViewCell *)[super collectionView:collectionView cellForItemAtIndexPath:indexPath];
    
    if (cell.textView != nil) {
        cell.textView.textColor = [UIColor whiteColor];
        [cell.textView setEditable:NO];
        
        cell.textView.linkTextAttributes = @{ NSForegroundColorAttributeName : cell.textView.textColor,
                                          NSUnderlineStyleAttributeName : @(NSUnderlineStyleSingle | NSUnderlinePatternSolid) };
    }
    return cell;
}

- (BOOL) previousMessageHaveDifferentDate:(NSInteger) index {
    
    if (index >= [self.messages count]) {
        return NO;
    }
    if (index == 0) {
        return YES;
    }
    
    JSQMessage *currentMessage = [self.messages objectAtIndex:index];
    JSQMessage *previousMessage = [self.messages objectAtIndex:index - 1];
    
    NSDateComponents *currentComponents = [[NSCalendar currentCalendar] components:NSCalendarUnitDay|NSCalendarUnitMonth|NSCalendarUnitYear fromDate:currentMessage.date];
    NSDateComponents *previousComponents = [[NSCalendar currentCalendar] components:NSCalendarUnitDay|NSCalendarUnitMonth|NSCalendarUnitYear fromDate:previousMessage.date];

    
    if (currentComponents.year == previousComponents.year &&
        currentComponents.day == previousComponents.day &&
        currentComponents.month == previousComponents.month) {
        return NO;
    }
    else {
        return YES;
    }
}


#pragma mark - JSQMessages collection view flow layout delegate

- (CGFloat)collectionView:(JSQMessagesCollectionView *)collectionView
                   layout:(JSQMessagesCollectionViewFlowLayout *)collectionViewLayout heightForCellTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
    if ([self previousMessageHaveDifferentDate:indexPath.item]) {
        return kJSQMessagesCollectionViewCellLabelHeightDefault;
    }
    
    return 0.0f;
}

- (CGFloat)collectionView:(JSQMessagesCollectionView *)collectionView
                   layout:(JSQMessagesCollectionViewFlowLayout *)collectionViewLayout heightForMessageBubbleTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
    /**
     *  iOS7-style sender name labels
     */
    JSQMessage *currentMessage = [self.messages objectAtIndex:indexPath.item];
    if ([[currentMessage senderId] isEqualToString:self.senderId]) {
        return 0.0f;
    }
    
    if (indexPath.item - 1 > 0) {
        JSQMessage *previousMessage = [self.messages objectAtIndex:indexPath.item - 1];
        if ([[previousMessage senderId] isEqualToString:[currentMessage senderId]]) {
            return 0.0f;
        }
    }
    
    return 0.0f;
}

-(void)collectionView:(JSQMessagesCollectionView *)collectionView didTapMessageBubbleAtIndexPath:(NSIndexPath *)indexPath {
    JSQMessage* message = [self.messages objectAtIndex:indexPath.item];

    if (message) {
        if (message.isMediaMessage) {
            id mediaItem = message.media;
            
            if ([mediaItem isMemberOfClass:[JSQPhotoMediaItem class]]) {
                JSQPhotoMediaItem* photo = (JSQPhotoMediaItem*)mediaItem;
                chatShowImage = photo.image;
                
                EBPhotoPagesController *photoPagesController = [[EBPhotoPagesController alloc]
                                                                initWithDataSource:self delegate:self];
                
                [self presentViewController:photoPagesController animated:YES completion:nil];
            }
            else if ([mediaItem isMemberOfClass:[JSQLocationMediaItem class]]) {
                JSQLocationMediaItem* location = (JSQLocationMediaItem*)mediaItem;
                
                MKPlacemark* place = [[MKPlacemark alloc] initWithCoordinate: location.location.coordinate addressDictionary: nil];
                MKMapItem* destination = [[MKMapItem alloc] initWithPlacemark: place];
                destination.name = [NSString stringWithFormat: @"%@ location", message.senderDisplayName, nil];
                NSArray* items = [[NSArray alloc] initWithObjects: destination, nil];
                NSDictionary* options = [[NSDictionary alloc] initWithObjectsAndKeys:
                                         MKLaunchOptionsDirectionsModeWalking,
                                         MKLaunchOptionsDirectionsModeKey, nil];
                [MKMapItem openMapsWithItems: items launchOptions: options];
            }
        }
    }
}

-(void)collectionView:(JSQMessagesCollectionView *)collectionView didLongPressMessageBubbleAtIndexPath:(NSIndexPath *)indexPath {
    JSQMessage* message = [self.messages objectAtIndex:indexPath.item];
    JSQMessagesCollectionViewCell *cell = (JSQMessagesCollectionViewCell *)[super collectionView:collectionView cellForItemAtIndexPath:indexPath];
    
    if (message && message.isMediaMessage) {
        UIMenuController* menuController = [UIMenuController sharedMenuController];
        UIMenuItem *listMenuItem = [[UIMenuItem alloc] initWithTitle:@"Delete" action:@selector(delete:)];
        
        [menuController setMenuItems:[NSArray arrayWithObject:listMenuItem]];
        [menuController setTargetRect:cell.messageBubbleContainerView.frame inView: self.view];
        [menuController setMenuVisible:YES animated:YES];
    }
}

- (CGFloat)collectionView:(JSQMessagesCollectionView *)collectionView
                   layout:(JSQMessagesCollectionViewFlowLayout *)collectionViewLayout heightForCellBottomLabelAtIndexPath:(NSIndexPath *)indexPath
{
    return kJSQMessagesCollectionViewCellLabelHeightDefault;
}

- (void)collectionView:(JSQMessagesCollectionView *)collectionView
                header:(JSQMessagesLoadEarlierHeaderView *)headerView didTapLoadEarlierMessagesButton:(UIButton *)sender
{
    NSLog(@"Load earlier messages!");
}

- (BOOL)collectionView:(UICollectionView *)collectionView canPerformAction:(SEL)action forItemAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender
{
    return (action == @selector(delete:));
}

- (void)collectionView:(UICollectionView *)collectionView performAction:(SEL)action forItemAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender
{
    if (action == @selector(delete:)) {
        [self deleteItemAtIndex:indexPath];
    }
}

- (void) deleteItemAtIndex:(NSIndexPath *)indexPath {
    NSInteger indexOfMessage = indexPath.item;
    [self.messages removeObjectAtIndex:indexOfMessage];
    [self.collectionView deleteItemsAtIndexPaths:@[indexPath]];
}

//actions
-(void) showShareMenu {
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Share location", @"Take photo", @"Choose existing photo" ,nil];
    actionSheet.actionSheetStyle = UIActionSheetStyleBlackOpaque;
    [actionSheet showInView:self.view];
}

-(void) actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0:
            [self shareMyLocation];
            break;
        case 1:
            [self takePhoto];
            break;
        case 2:
            [self shareExistingPhoto];
            break;
        default:
            break;
    }
}

- (void) shareMyLocation {
    CLLocation *userLocation = [LocationManagerSingleton sharedSingleton].locationManager.location;
    NSNumber* lon = [[NSNumber alloc] initWithDouble:userLocation.coordinate.longitude];
    NSNumber* lat = [[NSNumber alloc] initWithDouble:userLocation.coordinate.latitude];
    NSMutableDictionary* location = [[NSMutableDictionary alloc] init];
    
    [location setObject:lon forKey:@"lon"];
    [location setObject:lat forKey:@"lat"];
    
    [self sendMessage:@"Check my location!" url:@"" type:@"location" location:location];
    [self loadMessage:self.senderId displayName:self.senderDisplayName date:[NSDate date] message:@"" type:@"location" url:@"" location:location];
    [self finishReceivingMessage];
}

- (void) takePhoto {
    [picker setSourceType:UIImagePickerControllerSourceTypeCamera];
    [self presentViewController:picker animated:YES completion:NULL];
}

- (void) shareExistingPhoto {
    [picker setSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    [self presentViewController:picker animated:YES completion:NULL];
}

- (void)sendLike {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userName = [defaults objectForKey:@"userName"];
    
    [self sendMessage:[NSString stringWithFormat:@"%@ %@", userName, NSLocalizedString(@"send_like", nil)] url:@"" type:@"smile" location:nil];
}

//delegate methode will be called after picking photo either from camera or library
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage *tempImage = [info objectForKey:UIImagePickerControllerOriginalImage];
    [self dismissViewControllerAnimated:YES completion:^{
        [self uploadPhotoMessage:tempImage];
    }];
    
}

-(void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (void) didChat:(NSString*)status {
}

- (UIViewController *) documentInteractionControllerViewControllerForPreview: (UIDocumentInteractionController *) controller {
    return self;
}

#pragma mark - Image Datasource methods
- (UIImage *)photoPagesController:(EBPhotoPagesController *)controller
                     imageAtIndex:(NSInteger)index {
    return chatShowImage;
}

- (void)photoPagesController:(EBPhotoPagesController *)controller
                imageAtIndex:(NSInteger)index
           completionHandler:(void(^)(UIImage *image))handler {
    
}

- (BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController
    shouldExpectPhotoAtIndex:(NSInteger)index {
    if (index == 0) {
        return YES;
    }
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowCommentingForPhotoAtIndex:(NSInteger)index {
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowReportForPhotoAtIndex:(NSInteger)index {
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowMiscActionsForPhotoAtIndex:(NSInteger)index {
    return NO;
}

@end
