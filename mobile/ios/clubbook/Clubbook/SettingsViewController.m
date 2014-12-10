//
//  SettingsViewController.m
//  Clubbook
//
//  Created by Andrew on 8/4/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "SettingsViewController.h"
#import "SettingsCell.h"
#import "SessionHelper.h"
#import "WebViewController.h"
#import "CSNotificationView.h"

@interface SettingsViewController ()
{
    UIActionSheet *deletePopup;
    UIActionSheet *logoutPopup;
}

@property (nonatomic, strong) NSArray *settingsItems;
@end

@implementation SettingsViewController

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
    
    self.settingsItems = @[@"push", @"visible", @"privacy", @"terms", @"change", @"delete", @"contacts",@"faq", @"logout"];
    // Do any additional setup after loading the view.
    self.pushLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    self.visibleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    self.policyLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    self.termsLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    self.deleteLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    self.contactsLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    self.logoutLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userPush = [defaults objectForKey:@"userPush"];
    
    if ([userPush isEqualToString:@"true"]) {
        [self.pushSwitch setOn:YES animated:YES];
    } else {
        [self.pushSwitch setOn:NO animated:YES];
    }
    NSString *userVisible = [defaults objectForKey:@"userVisible"];
    
    if ([userVisible isEqualToString:@"true"]) {
        [self.visibleSwitch setOn:YES animated:YES];
    } else {
        [self.visibleSwitch setOn:NO animated:YES];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Settings Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSString *cellIdentifier = [self.settingsItems objectAtIndex:indexPath.row];
    
   // SettingsCell *cell =  [tableView dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
    
    if ([cellIdentifier isEqualToString:@"privacy"]){
        [self performSegueWithIdentifier:@"onWeb" sender:NSLocalizedString(@"privacyUrl", nil)];
    } else if ([cellIdentifier isEqualToString:@"terms"]) {
        [self performSegueWithIdentifier:@"onWeb" sender:NSLocalizedString(@"termsUrl", nil)];
    } else if ([cellIdentifier isEqualToString:@"change"]) {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        BOOL isFb = [defaults boolForKey:@"isFb"];
        if(!isFb) {
            [self performSegueWithIdentifier:@"onChange" sender:nil];
        } else {
            [CSNotificationView showInViewController:self
                                               style:CSNotificationViewStyleError
                                             message:NSLocalizedString(@"fbLoginCannotChangePass", nil)];
        }
        
    } else if ([cellIdentifier isEqualToString:@"delete"]) {
        deletePopup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSureDelete", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                                NSLocalizedString(@"yes", nil),
                                nil];
        [deletePopup showInView:[UIApplication sharedApplication].keyWindow];
    } else if ([cellIdentifier isEqualToString:@"contacts"]) {
        NSString *email = @"mailto:support@clubbook.com?subject=Question about clubbook";
        email = [email stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:email]];
    } else if ([cellIdentifier isEqualToString:@"logout"]){
        logoutPopup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSureLogOut", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                                NSLocalizedString(@"yes", nil),
                                nil];
        [logoutPopup showInView:[UIApplication sharedApplication].keyWindow];
    
    } else if([cellIdentifier isEqualToString:@"faq"])
    {
    [self performSegueWithIdentifier:@"onWeb" sender:NSLocalizedString(@"faqUrl", nil)];
    }
    [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSString *)sender
{
    if([[segue identifier] isEqualToString:@"onWeb"]){
    
        WebViewController *webController =  [segue destinationViewController];
        webController.web = NSLocalizedString(sender, nil);
    }
}

- (void)actionSheet:(UIActionSheet *)popup clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch (buttonIndex) {
        case 0:
        {
            // click yes
            if ([popup isEqual:logoutPopup]) {
                [SessionHelper DeleteUser];
                [self performSegueWithIdentifier:@"onLogout" sender:nil];
            } else {
                NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                NSString *accessToken = [defaults objectForKey:@"accessToken"];
               [self._manager deleteUser:accessToken];

            }

            break;
        }
        case 1:
            // click cancel
            break;
        default:
            break;
    }
    
}

- (void)didDeleteUser:(NSString *)result
{
    [SessionHelper DeleteUser];
    [self performSegueWithIdentifier:@"onLogout" sender:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)pushSwitchAction:(id)sender {
    BOOL state = [sender isOn];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    if (state) {
        [self._manager changeUserPush:accessToken push:YES];
    } else {
        [self._manager changeUserPush:accessToken push:NO];
    }
}
- (IBAction)visibleSwitchAction:(id)sender {
    BOOL state = [sender isOn];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    if (state) {
        [self._manager changeUserVisible:accessToken visible:YES];
    } else {
        [self._manager changeUserVisible:accessToken visible:NO];
    }
}

- (void)didChangePush:(User *)user;
{
    [SessionHelper StoreUser:user];
}

- (void)didChangeVisibleNearby:(User *)user;
{
    [SessionHelper StoreUser:user];
}
@end
