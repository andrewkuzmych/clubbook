//
//  LoginViewController.m
//  Clubbook
//
//  Created by Andrew on 6/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "LoginViewController.h"
#import "Validator.h"
#import "SessionHelper.h"
#import "CSNotificationView.h"

@interface LoginViewController (){
    User *_user;
}

@end

@implementation LoginViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Login";
    [self.navigationController setNavigationBarHidden:NO];

    UIToolbar *baseToolbar = [self generateToolbarForKeyboard];
    
    UIColor *placeholderColor = [UIColor colorWithRed:170/255.0 green:170/255.0 blue:170/255.0 alpha:1];
    
    self.emailText.inputAccessoryView = baseToolbar;
    self.emailText.delegate = self;
    self.emailText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"email", nil)
                                                                           attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0],
                                                                               NSForegroundColorAttributeName: placeholderColor}];

    self.passText.inputAccessoryView = baseToolbar;
    self.passText.delegate = self;
    self.passText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"pass", nil)
                                                                          attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0],
                                                                            NSForegroundColorAttributeName: placeholderColor}];

    self.loginButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
    [self.loginButton setTitle:NSLocalizedString(@"login", nil) forState:UIControlStateNormal];
    [self.forgotPassButton setTitle:NSLocalizedString(@"forgotPass", nil) forState:UIControlStateNormal];
    
    [self.emailText becomeFirstResponder];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Login Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)forgotPassAction:(id)sender {
    NSString *resetPassUrl = [NSString stringWithFormat:NSLocalizedString(@"resetPassUrl", nil), @(1000000)];
    
    NSURL *url = [ [ NSURL alloc ] initWithString: resetPassUrl];
    [[UIApplication sharedApplication] openURL:url];
}

- (IBAction)loginAction:(id)sender {
    NSString *wrongeEmail = [NSString stringWithFormat:NSLocalizedString(@"wrongeEmail", nil)];
    NSString *wrongPass = [NSString stringWithFormat:NSLocalizedString(@"wrongPass", nil)];
     
    BOOL isValidEmail = [Validator NSStringIsValidEmail:self.emailText.text];
    if(isValidEmail == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongeEmail];
        return;
    }
    
    BOOL isValidPass = [Validator NSStringLength:self.passText.text:6];
    if(isValidPass == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongPass];
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self showProgress:NO title:nil];
        [self._manager signinUser:self.emailText.text pass:self.passText.text];
    });

}

- (void)didSigninUser:(User *) user {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _user = user;
        
        if(_user.error != nil)
        {
            [self showAlert: _user.error title:@"Error"];
            
            return;
        }
        
        [SessionHelper StoreUser:_user];
        
        [self performSegueWithIdentifier: @"onLogin" sender: self];
    });
}




@end
