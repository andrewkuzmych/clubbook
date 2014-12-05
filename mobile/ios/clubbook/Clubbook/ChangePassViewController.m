//
//  ChangePassViewController.m
//  Clubbook
//
//  Created by Andrew on 9/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ChangePassViewController.h"
#import "Validator.h"
#import "SessionHelper.h"
#import "CSNotificationView.h"

@interface ChangePassViewController ()

@end

@implementation ChangePassViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.navigationController setNavigationBarHidden:NO];
    
    UIToolbar *baseToolbar = [self generateToolbarForKeyboard];
    
    UIColor *placeholderColor = [UIColor colorWithRed:170/255.0 green:170/255.0 blue:170/255.0 alpha:1];
    
    self.currentPassText.inputAccessoryView = baseToolbar;
    self.currentPassText.delegate = self;
    self.currentPassText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"currentPass", nil)
                                                                           attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0],
                                                                                        NSForegroundColorAttributeName: placeholderColor}];
    
    self.passText.inputAccessoryView = baseToolbar;
    self.passText.delegate = self;
    self.passText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"newPass", nil)
                                                                          attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0],
                                                                                       NSForegroundColorAttributeName: placeholderColor}];

    self.confirmNewPassText.inputAccessoryView = baseToolbar;
    self.confirmNewPassText.delegate = self;
    self.confirmNewPassText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"confirmNewPass", nil)
                                                                          attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0],
                                                                                       NSForegroundColorAttributeName: placeholderColor}];
    
    self.resetPassButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
    [self.resetPassButton setTitle:NSLocalizedString(@"resetPass", nil) forState:UIControlStateNormal];

    [self.currentPassText becomeFirstResponder];
    
    
    
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Chane Pass Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)resetPassAction:(id)sender {
    NSString *wrongPass = [NSString stringWithFormat:NSLocalizedString(@"wrongPass", nil)];
    NSString *passDidNotMatch = [NSString stringWithFormat:NSLocalizedString(@"passDidNotMatch", nil)];
    
    
    BOOL isValidPass = [Validator NSStringLength:self.currentPassText.text:6];
    if(isValidPass == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongPass];
        return;
    }
    
    isValidPass = [Validator NSStringLength:self.passText.text:6];
    if(isValidPass == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongPass];
        return;
    }
    
    if (![self.passText.text isEqualToString:self.confirmNewPassText.text])
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:passDidNotMatch];
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self showProgress:NO title:nil];
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        [self._manager changePass:self.currentPassText.text newPass:self.passText.text accessToken:accessToken];
    });

}

- (void)didChangePass:(BOOL) result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        NSString *wrongPass = [NSString stringWithFormat:NSLocalizedString(@"wrongPass", nil)];
        
        if (!result) {
            [CSNotificationView showInViewController:self
                                               style:CSNotificationViewStyleError
                                             message:wrongPass];
            return;
        }

        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleSuccess
                                         message:[NSString stringWithFormat:NSLocalizedString(@"passChanged", nil)]];

    });
    
}

@end
