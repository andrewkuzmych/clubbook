//
//  BaseViewController.h
//  Clubbook
//
//  Created by Andrew on 6/19/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ClubbookManager.h"
#import "BaseViewControllerHelper.h"
#import "GAITrackedViewController.h"
#import "SWRevealViewController.h"

@interface BaseViewController : GAITrackedViewController <ClubbookManagerDelegate>

@property (weak, nonatomic) IBOutlet UIBarButtonItem *sidebarButton;
@property (strong, nonatomic) ClubbookManager *_manager;

- (void)showProgress: (BOOL) clearContext title:(NSString*) title;
- (void)hideProgress;

@end
