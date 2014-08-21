//
//  BaseTableViewController.h
//  Clubbook
//
//  Created by Andrew on 6/21/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ClubbookManager.h"
#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"

@interface BaseTableViewController : UITableViewController<UITextFieldDelegate>

@property (strong, nonatomic) ClubbookManager *_manager;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *sidebarButton;
//@property (strong, nonatomic) UIActivityIndicatorView *spinner;
@property (strong, nonatomic) UITextField *currentFocus;

- (UIToolbar *)generateToolbarForKeyboard;
- (void)showAlert: (NSString *) message title:(NSString *) title;
- (void)showProgress: (BOOL) clearContext title:(NSString*) title;
- (void)hideProgress;
@end
