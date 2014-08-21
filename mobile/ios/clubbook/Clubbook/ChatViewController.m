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

@interface ChatViewController (){
    bool canChat;
    Chat *_chat;
}

@end

@implementation ChatViewController

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
    //self.title = @"Chat";
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userFrom = [defaults objectForKey:@"userId"];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        
        self.sender = userFrom;
        
        // retreve conversation
        [self._manager retrieveConversation:userFrom toUser:self.userTo accessToken:accessToken];
        [self showProgress:YES title:nil];
    });
    
    
    // add chat hot buttons (smile, drink)
    UIButton *smileButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [smileButton addTarget:self
                    action:@selector(sendSmile:)
          forControlEvents:UIControlEventTouchUpInside];
    [smileButton setTitle:@"Like" forState:UIControlStateNormal];
    [smileButton setTitleColor: [UIColor whiteColor] forState:UIControlStateNormal];
    [smileButton setTitleColor: [UIColor greenColor] forState:UIControlStateHighlighted];
    smileButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    
    UIButton *drinkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [drinkButton addTarget:self
                    action:@selector(sendDrink:)
          forControlEvents:UIControlEventTouchUpInside];
    [drinkButton setImage:[UIImage imageNamed:@"icon_chat_drink"] forState:UIControlStateNormal];
    
    self.inputToolbar.contentView.leftBarButtonItem = smileButton;
    
    self.inputToolbar.contentView.middleBarButtonItem = drinkButton;
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
    
    if ([user_to isEqualToString:self.sender]) {
        canChat = YES;
        [self putMessage:msg type:type sender:user_from];
        [self._manager readChat:_chat.currentUser.id toUser:_chat.receiver.id accessToken:accessToken];
    }
}

- (void)didChat:(NSString *)result
{
}

- (void)didReceiveConversation:(Chat *)chat
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _chat = chat;
        self.messages = [[NSMutableArray alloc] init];
        
        // get user to avatar
        CGFloat outgoingDiameter = self.collectionView.collectionViewLayout.outgoingAvatarViewSize.width;
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @256, @"height": @256, @"crop": @"thumb", @"gravity": @"face"}];
        CGFloat incomingDiameter = self.collectionView.collectionViewLayout.incomingAvatarViewSize.width;
        NSString * receiverUserUrl  = [cloudinary url: [chat.receiver.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        NSURL *receiverUserUrlImageURL = [NSURL URLWithString:receiverUserUrl];
        NSData *receiverUserData = [NSData dataWithContentsOfURL:receiverUserUrlImageURL];
        self.userToImage =  [JSQMessagesAvatarFactory avatarWithImage:[UIImage imageWithData:receiverUserData] diameter:incomingDiameter];
        
        NSString * senderUserUrl  = [cloudinary url: [chat.currentUser.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        NSURL *senderUserUrlImageURL = [NSURL URLWithString:senderUserUrl];
        NSData *senderUserData = [NSData dataWithContentsOfURL:senderUserUrlImageURL];
        self.userFromImage =  [JSQMessagesAvatarFactory avatarWithImage:[UIImage imageWithData:senderUserData] diameter:incomingDiameter];
        
        // set user to photo button
        UIImage *image =  [JSQMessagesAvatarFactory avatarWithImage:[UIImage imageWithData:receiverUserData] diameter:incomingDiameter];
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        button.bounds = CGRectMake( 0, 0, image.size.width, image.size.height );
        [button setImage:image forState:UIControlStateNormal];
        [button addTarget:self action:@selector(onUser:) forControlEvents:UIControlEventTouchUpInside];
        UIBarButtonItem *barButtonItem = [[UIBarButtonItem alloc] initWithCustomView:button];
        self.navigationItem.rightBarButtonItem=barButtonItem;
        
        self.title = chat.receiver.name;
        
        for(Conversation * conf in chat.conversations)
        {
            
            JSQMessage *jsqmessage =  [[JSQMessage alloc] initWithText:conf.msg sender:conf.user_from date:conf.time type:conf.type];
        
            [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
            [self.messages addObject:jsqmessage];
        }
        
        [self setCanChat];

        // chat bubbles
        self.outgoingBubbleImageView = [JSQMessagesBubbleImageFactory
                                        outgoingMessageBubbleImageViewWithColor:[UIColor jsq_messageBubbleLightGrayColor]];
        
        self.incomingBubbleImageView = [JSQMessagesBubbleImageFactory
                                        incomingMessageBubbleImageViewWithColor:[UIColor jsq_messageBubbleGreenColor]];
        
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
        userController.userId = _chat.receiver.id;
    }
}


- (void)setCanChat
{
    if ([self.messages count] < 3) {
        canChat = true;
        return;
    }
    
    for (JSQMessage * mess in self.messages ) {
        if ([mess.sender isEqualToString:self.userTo]) {
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
    

    if (self.delegateModal) {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:@selector(closePressed:)];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    /**
     *  Enable/disable springy bubbles, default is YES.
     *  For best results, toggle from `viewDidAppear:`
     */
    self.collectionView.collectionViewLayout.springinessEnabled = NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Actions

- (void)sendSmile:(UIButton *)senderElement
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userName = [defaults objectForKey:@"userName"];
    
    [self sendMessage:[NSString stringWithFormat:@"%@ %@", userName, NSLocalizedString(@"send_like", nil)] type:@"smile"];
}

- (void)sendDrink:(UIButton *)senderElement
{
    [self sendMessage:NSLocalizedString(@"invite_for_drink", nil) type:@"drink"];
}

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
    [self putMessage:message type:type sender:self.sender];
    
    
    NSString* trimMessage = [message stringByTrimmingCharactersInSet:
                              [NSCharacterSet whitespaceCharacterSet]];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    // send to server
    [self._manager chat:self.sender user_to:self.userTo msg:trimMessage msg_type:type accessToken:accessToken];
}

- (void)putMessage:(NSString *)message type:(NSString *)type sender:(NSString *) sender
{
    JSQMessage *jsqmessage =  [[JSQMessage alloc] initWithText:message sender:sender date:[NSDate date] type:type];
    if ([type isEqualToString:@"drink"]) {
        jsqmessage =  [[JSQMessage alloc] initWithText:message sender:sender date:[NSDate date] type:type];
    } else if ([type isEqualToString:@"smile"]) {
        jsqmessage =  [[JSQMessage alloc] initWithText:message sender:sender date:[NSDate date] type:type];
    }
    
    [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
    [self.messages addObject:jsqmessage];
    [self setCanChat];
    [self finishReceivingMessage];
}

- (void)closePressed:(UIBarButtonItem *)sender
{
    [self.delegateModal didDismissJSQDemoViewController:self];
}

#pragma mark - JSQMessagesViewController method overrides

- (void)didPressSendButton:(UIButton *)button
           withMessageText:(NSString *)text
                    sender:(NSString *)sender
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
    NSLog(@"Camera pressed!");
    /**
     *  Accessory button has no default functionality, yet.
     */
}

#pragma mark - JSQMessages CollectionView DataSource

- (id<JSQMessageData>)collectionView:(JSQMessagesCollectionView *)collectionView messageDataForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.messages objectAtIndex:indexPath.item];
}

- (UIImageView *)collectionView:(JSQMessagesCollectionView *)collectionView bubbleImageViewForItemAtIndexPath:(NSIndexPath *)indexPath
{
    /**
     *  You may return nil here if you do not want bubbles.
     *  In this case, you should set the background color of your collection view cell's textView.
     */
    
    /**
     *  Reuse created bubble images, but create new imageView to add to each cell
     *  Otherwise, each cell would be referencing the same imageView and bubbles would disappear from cells
     */
    
    JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
    
    if ([message.sender isEqualToString:self.sender]) {
        return [[UIImageView alloc] initWithImage:self.outgoingBubbleImageView.image
                                 highlightedImage:self.outgoingBubbleImageView.highlightedImage];
    }
    
    return [[UIImageView alloc] initWithImage:self.incomingBubbleImageView.image
                             highlightedImage:self.incomingBubbleImageView.highlightedImage];
}

- (UIImageView *)collectionView:(JSQMessagesCollectionView *)collectionView avatarImageViewForItemAtIndexPath:(NSIndexPath *)indexPath
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
    
    UIImage *avatarImage = self.userFromImage;

    if([message.sender isEqualToString:self.userTo])
       avatarImage=  self.userToImage;
    
    UIImageView * imageView = [[UIImageView alloc] initWithImage:avatarImage];
    
    return imageView;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForCellTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
    /**
     *  This logic should be consistent with what you return from `heightForCellTopLabelAtIndexPath:`
     *  The other label text delegate methods should follow a similar pattern.
     *
     *  Show a timestamp for every 3rd message
     */
    if (indexPath.item % 3 == 0) {
        JSQMessage *message = [self.messages objectAtIndex:indexPath.item];
        return [[JSQMessagesTimestampFormatter sharedFormatter] attributedTimestampForDate:message.date];
    }
    
    return nil;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForMessageBubbleTopLabelAtIndexPath:(NSIndexPath *)indexPath
{
     return nil;
}

- (NSAttributedString *)collectionView:(JSQMessagesCollectionView *)collectionView attributedTextForCellBottomLabelAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
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
    
    JSQMessage *msg = [self.messages objectAtIndex:indexPath.item];
    
    cell.textView.textColor = [UIColor whiteColor];
   
    /*if ([msg.sender isEqualToString:self.sender]) {
        cell.textView.textColor = [UIColor blackColor];
    }
    else {
        cell.textView.textColor = [UIColor whiteColor];
    }*/
    
    cell.textView.linkTextAttributes = @{ NSForegroundColorAttributeName : cell.textView.textColor,
                                          NSUnderlineStyleAttributeName : @(NSUnderlineStyleSingle | NSUnderlinePatternSolid) };
    return cell;
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
    if (indexPath.item % 3 == 0) {
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
    if ([[currentMessage sender] isEqualToString:self.sender]) {
        return 0.0f;
    }
    
    if (indexPath.item - 1 > 0) {
        JSQMessage *previousMessage = [self.messages objectAtIndex:indexPath.item - 1];
        if ([[previousMessage sender] isEqualToString:[currentMessage sender]]) {
            return 0.0f;
        }
    }
    
    return kJSQMessagesCollectionViewCellLabelHeightDefault;
}

- (CGFloat)collectionView:(JSQMessagesCollectionView *)collectionView
                   layout:(JSQMessagesCollectionViewFlowLayout *)collectionViewLayout heightForCellBottomLabelAtIndexPath:(NSIndexPath *)indexPath
{
    return 0.0f;
}

- (void)collectionView:(JSQMessagesCollectionView *)collectionView
                header:(JSQMessagesLoadEarlierHeaderView *)headerView didTapLoadEarlierMessagesButton:(UIButton *)sender
{
    NSLog(@"Load earlier messages!");
}

@end
