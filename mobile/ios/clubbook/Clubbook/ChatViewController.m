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

@interface ChatViewController ()

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
    [PubNub setDelegate:self];
    // Do any additional setup after loading the view.
    self.title = @"Chat";
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userFrom = [defaults objectForKey:@"userId"];
        self.sender = userFrom;
        
        // retreve conversation
        [self._manager retrieveConversation:userFrom toUser:self.userTo];
        [self showProgress:YES title:nil];
    });
    
    
    // add chat hot buttons (smile, drink)
    UIButton *smileButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [smileButton addTarget:self
                    action:@selector(sendSmile:)
          forControlEvents:UIControlEventTouchUpInside];
    [smileButton setBackgroundImage:[UIImage imageNamed:@"icon_smiley"] forState:UIControlStateNormal];
    
    UIButton *drinkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [drinkButton addTarget:self
                    action:@selector(sendDrink:)
          forControlEvents:UIControlEventTouchUpInside];
    [drinkButton setBackgroundImage:[UIImage imageNamed:@"icon_drink"] forState:UIControlStateNormal];
    
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
    
    if ([user_to isEqualToString:self.sender]) {
        [self putMessage:msg type:type sender:user_from];
    }
    
}

- (void)didChat:(NSString *)result
{
}

- (void)didReceiveConversation:(Chat *)chat
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
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
        
        
         // get user from avatar
        self.userFromImage = [JSQMessagesAvatarFactory avatarWithUserInitials:@"ME"
                                                               backgroundColor:[UIColor colorWithWhite:0.85f alpha:1.0f]
                                                                    textColor:[UIColor colorWithWhite:0.60f alpha:1.0f] font:[UIFont systemFontOfSize:14.0f] diameter:outgoingDiameter];
        
        
        for(Conversation * conf in chat.conversations)
        {
            JSQMessage *jsqmessage =  [[JSQMessage alloc] initWithText:conf.msg sender:conf.user_from date:conf.time type:conf.type];
            
            if ([conf.type isEqualToString:@"drink"]) {
                jsqmessage =  [[JSQMessage alloc] initWithText:[NSString stringWithFormat:@"     %@",NSLocalizedString(@"invite_for_drink", nil)] sender:conf.user_from date:conf.time type:conf.type];
            } else if ([conf.type isEqualToString:@"smile"]) {
                jsqmessage =  [[JSQMessage alloc] initWithText:[NSString stringWithFormat:@"     %@",NSLocalizedString(@"send_smile", nil)] sender:conf.user_from date:conf.time type:conf.type];
            }

        
            [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
            [self.messages addObject:jsqmessage];
        }

        // chat bubbles
        self.outgoingBubbleImageView = [JSQMessagesBubbleImageFactory
                                        outgoingMessageBubbleImageViewWithColor:[UIColor jsq_messageBubbleLightGrayColor]];
        
        self.incomingBubbleImageView = [JSQMessagesBubbleImageFactory
                                        incomingMessageBubbleImageViewWithColor:[UIColor jsq_messageBubbleGreenColor]];
        
        [self finishReceivingMessage];
        
        // mark chat as read
        [self._manager readChat:chat.currentUser.id toUser:chat.receiver.id];
        
    });
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if (self.delegateModal) {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:@selector(closePressed:)];
    }
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    /**
     *  Enable/disable springy bubbles, default is YES.
     *  For best results, toggle from `viewDidAppear:`
     */
    self.collectionView.collectionViewLayout.springinessEnabled = YES;
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Actions

- (void)sendSmile:(UIButton *)senderElement
{
    [self sendMessage:NSLocalizedString(@"send_smile", nil) type:@"smile"];
}

- (void)sendDrink:(UIButton *)senderElement
{
    [self sendMessage:NSLocalizedString(@"invite_for_drink", nil) type:@"drink"];
}

- (void)sendMessage:(NSString *)message type:(NSString *)type
{
    [self putMessage:message type:type sender:self.sender];
    
    // send to server
    [self._manager chat:self.sender user_to:self.userTo msg:message msg_type:type];
}
- (void)putMessage:(NSString *)message type:(NSString *)type sender:(NSString *) sender
{
    JSQMessage *jsqmessage =  [[JSQMessage alloc] initWithText:message sender:sender date:[NSDate date] type:type];
    if ([type isEqualToString:@"drink"]) {
        jsqmessage =  [[JSQMessage alloc] initWithText:@"     invite you for a drink" sender:sender date:[NSDate date] type:type];
    } else if ([type isEqualToString:@"smile"]) {
        jsqmessage =  [[JSQMessage alloc] initWithText:@"     send you smile" sender:sender date:[NSDate date] type:type];
    }
    
    [JSQSystemSoundPlayer jsq_playMessageReceivedSound];
    [self.messages addObject:jsqmessage];
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
    
    if ([msg.sender isEqualToString:self.sender]) {
        cell.textView.textColor = [UIColor blackColor];
    }
    else {
        cell.textView.textColor = [UIColor whiteColor];
    }
    
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