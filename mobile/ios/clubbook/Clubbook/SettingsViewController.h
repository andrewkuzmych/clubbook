//
//  SettingsViewController.h
//  Clubbook
//
//  Created by Andrew on 8/4/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseTableViewController.h"

@interface SettingsViewController : BaseTableViewController<UIActionSheetDelegate>
@property (weak, nonatomic) IBOutlet UILabel *visibleLabel;
@property (weak, nonatomic) IBOutlet UILabel *pushLabel;
@property (weak, nonatomic) IBOutlet UILabel *policyLabel;
@property (weak, nonatomic) IBOutlet UILabel *termsLabel;
@property (weak, nonatomic) IBOutlet UILabel *deleteLabel;
@property (weak, nonatomic) IBOutlet UILabel *contactsLabel;
@property (weak, nonatomic) IBOutlet UISwitch *visibleSwitch;
@property (weak, nonatomic) IBOutlet UILabel *logoutLabel;
@property (weak, nonatomic) IBOutlet UISwitch *pushSwitch;
- (IBAction)pushSwitchAction:(id)sender;

@end
