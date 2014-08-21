//
//  ChatViewController.h
//  Clubbook
//
//  Created by Andrew on 7/7/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "JSQMessagesViewController.h"
#import "BaseMessagesViewController.h"

#import "JSQMessages.h"

@class JSQDemoViewController;


@protocol JSQDemoViewControllerDelegate <NSObject>

- (void)didDismissJSQDemoViewController:(JSQDemoViewController *)vc;

@end


@interface ChatViewController : BaseMessagesViewController<PNDelegate>

@property (assign, nonatomic) BOOL isFromUser;

@property (strong, nonatomic) NSString *userTo;
@property (weak, nonatomic) id<JSQDemoViewControllerDelegate> delegateModal;

@property (strong, nonatomic) NSMutableArray *messages;

@property (strong, nonatomic) UIImage *userFromImage;
@property (strong, nonatomic) UIImage *userToImage;

@property (strong, nonatomic) UIImageView *outgoingBubbleImageView;
@property (strong, nonatomic) UIImageView *incomingBubbleImageView;

- (void)receiveMessagePressed:(UIBarButtonItem *)sender;

- (void)closePressed:(UIBarButtonItem *)sender;

- (void)populateMessages;

@end
