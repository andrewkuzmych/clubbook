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
}

@end

@implementation ChatViewController
{
    UIImagePickerController* picker;
}
#pragma mark - Demo setup

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
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    [PubNub setDelegate:self];
    // Do any additional setup after loading the view.
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userFrom = [defaults objectForKey:@"userId"];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        
        self.senderId = userFrom;
        
        // retreve conversation
        [self._manager retrieveConversation:userFrom toUser:self.userTo accessToken:accessToken];
        [self showProgress:YES title:nil];
    });
    
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
    
    picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    
    _docController = nil;
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message {
    NSDictionary *dataJson = [message.message valueForKey:@"data"];
    NSDictionary *messageJson = [dataJson valueForKey:@"last_message"];
    NSString *msg = [messageJson valueForKey:@"msg"];
    NSString *type = [messageJson valueForKey:@"type"];
    NSString *user_from = [dataJson valueForKey:@"user_from"];
    NSString *user_to = [dataJson valueForKey:@"user_to"];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    if ([user_to isEqualToString:self.senderId] && [user_from isEqualToString:self.userTo]) {
        canChat = YES;
        [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
        [self putMessage:msg type:type sender:user_from];
        [self._manager readChat:_chat.currentUser.id toUser:_chat.receiver.id accessToken:accessToken];
    }
}

-(NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskAll;
}

- (BOOL)shouldAutorotate {
    return YES;
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
        CGFloat outgoingDiameter = self.collectionView.collectionViewLayout.outgoingAvatarViewSize.width;
        
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @256, @"height": @256, @"crop": @"thumb", @"gravity": @"face"}];
        
        NSString * receiverUserUrl  = [cloudinary url: [chat.receiver.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        NSURL *receiverUserUrlImageURL = [NSURL URLWithString:receiverUserUrl];
        NSData *receiverUserData = [NSData dataWithContentsOfURL:receiverUserUrlImageURL];
        self.companionAvatar =  [JSQMessagesAvatarImageFactory avatarImageWithImage:[UIImage imageWithData:receiverUserData] diameter:incomingDiameter];
        
        NSString * senderUserUrl  = [cloudinary url: [chat.currentUser.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        NSURL *senderUserUrlImageURL = [NSURL URLWithString:senderUserUrl];
        NSData *senderUserData = [NSData dataWithContentsOfURL:senderUserUrlImageURL];
        self.userAvatar =  [JSQMessagesAvatarImageFactory avatarImageWithImage:[UIImage imageWithData:senderUserData] diameter:outgoingDiameter];
        
        // set user to photo button
        UIImage *image =  [JSQMessagesAvatarImageFactory avatarImageWithImage:[UIImage imageWithData:receiverUserData] diameter:incomingDiameter].avatarImage;
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.bounds = CGRectMake( 0, 0, image.size.width, image.size.height );
        [button setImage:image forState:UIControlStateNormal];
        [button addTarget:self action:@selector(onUser:) forControlEvents:UIControlEventTouchUpInside];
        UIBarButtonItem *barButtonItem = [[UIBarButtonItem alloc] initWithCustomView:button];
        self.navigationItem.rightBarButtonItem = barButtonItem;
       
        for(Conversation * conf in chat.conversations) {
            [self loadMessage:conf.user_from displayName:conf.user_from date:conf.time message:conf.msg type:conf.type];
        }
        
        [self setCanChat];

        // chat bubbles
        JSQMessagesBubbleImageFactory* factory = [[JSQMessagesBubbleImageFactory alloc] init];
        
        self.companionBubble = [factory incomingMessagesBubbleImageWithColor:[UIColor jsq_messageBubbleLightGrayColor]];
        
        self.userBubble = [factory outgoingMessagesBubbleImageWithColor:[UIColor jsq_messageBubbleGreenColor]];
        
        [self finishReceivingMessage];
        
        // mark chat as read
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
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

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];

    self.collectionView.collectionViewLayout.springinessEnabled = NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Actions

- (void)sendMessage:(NSString *)message type:(NSString *)type
{
    if (!canChat) {
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"chat_limit", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    
    [JSQSystemSoundPlayer jsq_playMessageSentSound];
    [self putMessage:message type:type sender:self.senderId];
    
    
    NSString* trimMessage = [message stringByTrimmingCharactersInSet:
                              [NSCharacterSet whitespaceCharacterSet]];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    // send to server
    [self._manager chat:self.senderId user_to:self.userTo msg:trimMessage msg_type:type accessToken:accessToken];
}

- (void)loadMessage:(NSString*) sender displayName:(NSString*)displayName date:(NSDate*)date message:(NSString*)message type:(NSString*)type {
    if ([type isEqualToString:@"photo"]) {
        // transform avatar
       
        NSData* data = [[NSData alloc] initWithContentsOfURL:[NSURL URLWithString:message]];
        UIImage* image = [[UIImage alloc] initWithData:data];

        JSQPhotoMediaItem *photoItem = [[JSQPhotoMediaItem alloc] initWithImage:image andFilePath:message];
        JSQMessage *photoMessage = [JSQMessage messageWithSenderId:sender
                                                           displayName:displayName
                                                                 media:photoItem];
        [self.messages addObject:photoMessage];
        [self.collectionView reloadData];
    }
    else {
        JSQMessage *jsqmessage = [[JSQMessage alloc] initWithSenderId:sender senderDisplayName:displayName date:date text:message];
        [self.messages addObject:jsqmessage];
    }
}

- (void)putMessage:(NSString *)message type:(NSString *)type sender:(NSString *) sender {
    [self loadMessage:sender displayName:self.senderDisplayName date:[NSDate date] message:message type:type];
    [self setCanChat];
    [self finishReceivingMessage];
}

#pragma mark - JSQMessagesViewController method overrides

- (void)didPressSendButton:(UIButton *)button
           withMessageText:(NSString *)text
                  senderId:(NSString *)senderId
         senderDisplayName:(NSString *)senderDisplayName
                      date:(NSDate *)date
{
    /**
     *  Sending a message. Your implementation of this method should do *at least* the following:
     *
     *  1. Play sound (optional)
     *  2. Add new id<JSQMessageData> object to your data source
     *  3. Call `finishSendingMessage`
     */
    [self finishSendingMessage];
    [self sendMessage:text type:@"message"];
    
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
    /**
     *  You may return nil here if you do not want bubbles.
     *  In this case, you should set the background color of your collection view cell's textView.
     */
    
    /**
     *  Reuse created bubble images, but create new imageView to add to each cell
     *  Otherwise, each cell would be referencing the same imageView and bubbles would disappear from cells
     */
    
    JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
    
    if ([message.senderId isEqualToString:self.senderId]) {
        return self.userBubble;
    }
    
    return self.companionBubble;
}

- (id<JSQMessageAvatarImageDataSource>)collectionView:(JSQMessagesCollectionView *)collectionView avatarImageDataForItemAtIndexPath:(NSIndexPath *)indexPath;
{
    /**
     *  Return `nil` here if you do not want avatars.
     *  If you do return `nil`, be sure to do the following in `viewDidLoad`:
     *
     *  self.collectionView.collectionViewLayout.incomingAvatarViewSize = CGSizeZero;
     *  self.collectionView.collectionViewLayout.outgoingAvatarViewSize = CGSizeZero;
     *
     *  It is possible to have only outgoing avatars or only incoming avatars, too.
     */
    
    /**
     *  Reuse created avatar images, but create new imageView to add to each cell
     *  Otherwise, each cell would be referencing the same imageView and avatars would disappear from cells
     *
     *  Note: these images will be sized according to these values:
     *
     *  self.collectionView.collectionViewLayout.incomingAvatarViewSize
     *  self.collectionView.collectionViewLayout.outgoingAvatarViewSize
     *
     *  Override the defaults in `viewDidLoad`
     */
    
    JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
    
    JSQMessagesAvatarImage *avatarImage = self.userAvatar;

    if([message.senderId isEqualToString:self.userTo])
       avatarImage =  self.companionAvatar;
    
    return avatarImage;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForCellTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
    /**
     *  This logic should be consistent with what you return from `heightForCellTopLabelAtIndexPath:`
     *  The other label text delegate methods should follow a similar pattern.
     *
     *  Show a timestamp for every 3rd message
     */
    if ([self previousMessageHaveDifferentDate:indexPath.item]) {
        JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
        NSString* dateOnly = [[JSQMessagesTimestampFormatter sharedFormatter] relativeDateForDate:message.date];
        return [[NSAttributedString alloc] initWithString:dateOnly];
    }
    
    return nil;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForMessageBubbleTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
     return nil;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForCellBottomLabelAtIndexPath:(NSIndexPath *)indexPath
{
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
    /**
     *  Override point for customizing cells
     */
    JSQMessagesCollectionViewCell *cell = (JSQMessagesCollectionViewCell *)[super collectionView:collectionView cellForItemAtIndexPath:indexPath];
    
    /**
     *  Configure almost *anything* on the cell
     *
     *  Text colors, label text, label colors, etc.
     *
     *
     *  DO NOT set `cell.textView.font` !
     *  Instead, you need to set `self.collectionView.collectionViewLayout.messageBubbleFont` to the font you want in `viewDidLoad`
     *
     *
     *  DO NOT manipulate cell layout information!
     *  Instead, override the properties you want on `self.collectionView.collectionViewLayout` from `viewDidLoad`
     */
    
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
    /**
     *  Each label in a cell has a `height` delegate method that corresponds to its text dataSource method
     */
    
    /**
     *  This logic should be consistent with what you return from `attributedTextForCellTopLabelAtIndexPath:`
     *  The other label height delegate methods should follow similarly
     *
     *  Show a timestamp for every 3rd message
     */
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
                NSURL *URL = [NSURL fileURLWithPath:photo.photoURL];
                //NSURL *URL = [[NSBundle mainBundle] URLForResource:@"logo" withExtension:@"png"];
                if (URL) {
                    // Initialize Document Interaction Controller
                    _docController = [UIDocumentInteractionController interactionControllerWithURL:URL];
                    _docController.name = @"";
                    // Configure Document Interaction Controller
                    [_docController setDelegate:self];

                    [_docController presentPreviewAnimated:YES];
                }
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
    __weak UICollectionView *weakView = self.collectionView;
    CLLocation *userLocation = [LocationManagerSingleton sharedSingleton].locationManager.location;
    
    JSQLocationMediaItem *locationItem = [[JSQLocationMediaItem alloc] init];
    
    [locationItem setLocation:userLocation withCompletionHandler:^{
        [weakView reloadData];
    }];
    
    JSQMessage *locationMessage = [JSQMessage messageWithSenderId:self.senderId
                                                      displayName:self.senderId
                                                            media:locationItem];
    [self.messages addObject:locationMessage];
    [self.collectionView reloadData];
    [self scrollToBottomAnimated:YES];
    
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
    
    [self sendMessage:[NSString stringWithFormat:@"%@ %@", userName, NSLocalizedString(@"send_like", nil)] type:@"smile"];
}

//delegate methode will be called after picking photo either from camera or library
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage *tempImage = [info objectForKey:UIImagePickerControllerOriginalImage];
    UIImage *image = [tempImage fixOrientation];
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                         NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"hh-mm-dd-MM"];
    NSString *dateFromString = [dateFormatter stringFromDate:[NSDate date]];
    NSString *fileName = [NSString stringWithFormat:@"clubbook_photo_%@.jpg", dateFromString, nil];
    
    NSString *filePath =  [documentsDirectory stringByAppendingPathComponent:fileName];
    NSLog (@"New photo file = %@", filePath);
    
    // Get PNG data from following method
    NSData *myData = UIImageJPEGRepresentation(image, 0);
    // It is better to get JPEG data because jpeg data will store the location and other related information of image.
    [myData writeToFile:filePath atomically:YES];
    
    // upload image to coudinary
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLUploader* uploader = [[CLUploader alloc] init:cloudinary delegate:self];
    
     [uploader upload:myData options:@{} withCompletion:^(NSDictionary *successResult, NSString *errorResult, NSInteger code, id context) {
        if (successResult) {

            NSString *photoResult = [successResult objectForKey:@"url"];
            [self sendMessage:photoResult type:@"photo"];
        } 
    } andProgress:^(NSInteger bytesWritten, NSInteger totalBytesWritten, NSInteger totalBytesExpectedToWrite, id context) {
        NSLog(@"Block upload progress: %ld/%ld (+%ld)", (long)totalBytesWritten, (long)totalBytesExpectedToWrite, (long)bytesWritten);
    }];
    
    [self dismissViewControllerAnimated:YES completion:NULL];
}

-(void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (void) didChat:(NSString*)status {

}

- (UIViewController *) documentInteractionControllerViewControllerForPreview: (UIDocumentInteractionController *) controller {
    return self;
}

- (void) canRotate {
    
}
@end
