//
//  ChatViewController.h
//  Clubbook
//
//  Created by Andrew on 7/7/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "JSQMessagesViewController.h"
#import "BaseMessagesViewController.h"
#import "CLUploader.h"

#import "JSQMessages.h"

@class JSQDemoViewController;


@protocol JSQDemoViewControllerDelegate <NSObject>

- (void)didDismissJSQDemoViewController:(JSQDemoViewController *)vc;

@end


@interface ChatViewController : BaseMessagesViewController<PNDelegate, UIDocumentInteractionControllerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIActionSheetDelegate, CLUploaderDelegate>

@property (assign, nonatomic) BOOL isFromUser;

@property (strong, nonatomic) NSString *userTo;
@property (weak, nonatomic) id<JSQDemoViewControllerDelegate> delegateModal;

@property (strong, nonatomic) NSMutableArray *messages;

@property (strong, nonatomic) JSQMessagesAvatarImage *companionAvatar;
@property (strong, nonatomic) JSQMessagesAvatarImage *userAvatar;

@property (strong, nonatomic) JSQMessagesBubbleImage *userBubble;
@property (strong, nonatomic) JSQMessagesBubbleImage *companionBubble;

@property (nonatomic, retain) UIDocumentInteractionController *docController;

- (void)canRotate;

@end
