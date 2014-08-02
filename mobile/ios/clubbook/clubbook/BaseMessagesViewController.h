//
//  BaseMessagesViewController.h
//  Clubbook
//
//  Created by Andrew on 7/8/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "JSQMessagesViewController.h"
#import "ClubbookManager.h"

@interface BaseMessagesViewController : JSQMessagesViewController

@property (strong, nonatomic) ClubbookManager *_manager;

- (void)initUI;
- (void)showProgress: (BOOL) clearContext title:(NSString*) title;
- (void)hideProgress;

@end
