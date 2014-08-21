//
//  RegViewController.m
//  Clubbook
//
//  Created by Andrew on 6/20/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "RegViewController.h"
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Validator.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "Convertor.h"
#import "User.h"
#import "SessionHelper.h"
#import "CSNotificationView.h"

@interface RegViewController () <ClubbookManagerDelegate>{
    User *_user;
}

@property (strong, nonatomic) UIPickerView *genderPicker;
@property (strong, nonatomic) UIPickerView *countryPicker;
@property (strong, nonatomic) UIDatePicker *datePicker;
@property (strong, nonatomic) NSArray *genderPickerArray;
@property (strong, nonatomic) NSArray *countryPickerArray;
@property (strong, nonatomic) UIImage *avatar;
@property(nonatomic,retain) NSString* dobString;
@property(nonatomic,retain) NSString* gender;

@end

@implementation RegViewController

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
 
    [self.passText setDelegate:self];
    [self.navigationController setNavigationBarHidden:NO];

    UIToolbar *baseToolbar = [self generateToolbarForKeyboard];
    
    UIColor *placeholderColor = [UIColor colorWithRed:170/255.0 green:170/255.0 blue:170/255.0 alpha:1];
    
    self.nameText.inputAccessoryView = baseToolbar;
    self.nameText.delegate = self;
    self.nameText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"first_name", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0],
                                                                                       NSForegroundColorAttributeName: placeholderColor}];

    
    self.passText.inputAccessoryView = baseToolbar;
    self.passText.delegate = self;
    self.passText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"pass", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0],
                                                                                       NSForegroundColorAttributeName: placeholderColor}];
    
    self.emailText.inputAccessoryView = baseToolbar;
    self.emailText.delegate = self;
    self.emailText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"email", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.datePicker   = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 210, 320, 216)];
    [self.datePicker setDatePickerMode:UIDatePickerModeDate];
    self.datePicker.backgroundColor = [UIColor whiteColor];
    [self.datePicker addTarget:self action:@selector(dboChanged:) forControlEvents:UIControlEventValueChanged];
    
    self.dobText.inputView  = self.datePicker;
    self.dobText.delegate = self;
    self.dobText.inputAccessoryView = baseToolbar;
    self.dobText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"birthday", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.countryPickerArray = [NSArray arrayWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Countries" ofType:@"plist"]];
    self.countryPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    
    self.countryPicker.delegate = self;
    self.countryPicker.dataSource = self;
    
    self.countryText.inputView = self.countryPicker;
    //self.gender = @"male";
    self.countryText.delegate = self;
    self.countryText.inputAccessoryView = baseToolbar;
    self.countryText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Country" attributes:@{ NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.genderPickerArray = [[NSArray alloc] initWithObjects:NSLocalizedString(@"male", nil), NSLocalizedString(@"female", nil), nil];
    self.genderPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];

    self.genderPicker.delegate = self;
    self.genderPicker.dataSource = self;
    
    self.genderName.inputView = self.genderPicker;
    self.genderName.text = NSLocalizedString(@"birthday", nil);
    self.gender = @"male";
    self.genderName.delegate = self;
    self.genderName.inputAccessoryView = baseToolbar;
    self.genderName.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Gender" attributes:@{ NSFontAttributeName : [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    
    // Selects the row in the specified component
    [self.genderPicker selectRow:0 inComponent:0 animated:NO];
    [self pickerView:self.genderPicker didSelectRow:0 inComponent:0];

    
    self.regButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
    
    [self.regButton setTitle: NSLocalizedString(@"signUp", nil) forState:UIControlStateNormal];
    
    [self.nameText becomeFirstResponder];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Registration Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

#pragma mark - Picker delegate stuff

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    
    if([pickerView isEqual: self.countryPicker]){
        return self.countryPickerArray.count;
    }else if([pickerView isEqual: self.genderPicker]){
        return self.genderPickerArray.count;
    }
    return 0;
}

-(NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row   forComponent:(NSInteger)component
{
    if([pickerView isEqual: self.countryPicker]){
        return [[self.countryPickerArray objectAtIndex:row] objectForKey:@"name"];
    }else if([pickerView isEqual: self.genderPicker]){
        return [self.genderPickerArray objectAtIndex:row];
    }
    
    return nil;

}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row   inComponent:(NSInteger)component
{
    if([pickerView isEqual: self.countryPicker]){
        self.countryText.text = [[self.countryPickerArray objectAtIndex:row] objectForKey:@"name"];
    }else if([pickerView isEqual: self.genderPicker]){
        self.gender = @"male";
        if (row == 1) {
            self.gender = @"female";
        }
        
        self.genderName.text = [self.genderPickerArray objectAtIndex:row];
    }
}


-(void)clearNumberPad{
    [self.nameText resignFirstResponder];
    self.nameText.text = @"";
}

-(void)dboChanged:(id)sender{
    NSDate *dateToTExtField=[_datePicker date];
    NSDateFormatter *format = [[NSDateFormatter alloc] init];
    [format setDateFormat:@"dd MMM yyyy"];
    NSString* finalDateString = [format stringFromDate:dateToTExtField];
    
    NSDateFormatter *shortFormat = [[NSDateFormatter alloc] init];
    [shortFormat setDateFormat:@"dd.MM.yyyy"];
    
    _dobString = [shortFormat stringFromDate:dateToTExtField];

    self.dobText.text=finalDateString;
}

- (IBAction)addProfileImage:(id)sender {
    if (([UIImagePickerController isSourceTypeAvailable:
          UIImagePickerControllerSourceTypePhotoLibrary] == NO))
        return;
    
    UIImagePickerController *mediaUI = [[UIImagePickerController alloc] init];
    mediaUI.navigationBar.tintColor = [UIColor whiteColor];
    mediaUI.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    mediaUI.allowsEditing = YES;
    mediaUI.delegate = self;
    [self presentViewController:mediaUI animated:YES completion:nil];
}

- (void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    self.avatar = (UIImage *) [info objectForKey:UIImagePickerControllerEditedImage];
    [self dismissViewControllerAnimated:YES completion:^{
        // Do something with the image
    }];
    
    [self.profileImage setBackgroundImage:self.avatar forState:UIControlStateNormal];
}
- (IBAction)registerAction:(id)sender {
    
    NSString *wrongName = [NSString stringWithFormat:NSLocalizedString(@"wrongName", nil)];
    NSString *wrongeEmail = [NSString stringWithFormat:NSLocalizedString(@"wrongeEmail", nil)];
    NSString *wrongDob = [NSString stringWithFormat:NSLocalizedString(@"wrongDob", nil)];
    NSString *wrongPass = [NSString stringWithFormat:NSLocalizedString(@"wrongPass", nil)];
    NSString *wrongGender = [NSString stringWithFormat:NSLocalizedString(@"wrongGender", nil)];
    NSString *wrongCountry = [NSString stringWithFormat:NSLocalizedString(@"wrongCountry", nil)];
    NSString *wrongAvatar = [NSString stringWithFormat:NSLocalizedString(@"wrongAvatar", nil)];
    NSString *error = [NSString stringWithFormat:NSLocalizedString(@"error", nil)];
    
    BOOL isValidName = [Validator NSStringLength:self.nameText.text:2];
    if(isValidName == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongName];
        return;
    }
    
    BOOL isValidGender = [Validator NSStringLength:self.genderName.text:4];
    if(isValidGender == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongGender];
        return;
    }
    
    BOOL isValidCountry = [Validator NSStringLength:self.countryText.text:2];
    if(isValidCountry == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongCountry];
        return;
    }
    
    BOOL isValidDob = [Validator NSStringLength:_dobString:6];
    if(isValidDob == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongDob];
        return;
    }
     
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
    
    if (!self.avatar) {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongAvatar];
        return;
    }
    
    // upload data to server and image server
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLUploader* uploader = [[CLUploader alloc] init:cloudinary delegate:self];
    
    [self showProgress:NO title:nil];
    NSData *avatarData = UIImageJPEGRepresentation(self.avatar, 1.0);
    //NSString *avatarUrl = [NSString stringWithFormat:@"https://graph.facebook.com/%@/picture?width=700&height=700", user.id];
    [uploader upload:avatarData options:@{} withCompletion:^(NSDictionary *successResult, NSString *errorResult, NSInteger code, id context) {
        if (successResult) {
            dispatch_async(dispatch_get_main_queue(), ^{
                NSString *avatarResult = [Convertor convertDictionaryToJsonString:successResult];
                [self._manager signupUser:self.nameText.text email:self.emailText.text gender:self.gender city:@"Amsterdam" pass:self.passText.text dob:_dobString avatar:avatarResult country:self.countryText.text bio:NSLocalizedString(@"defoutBio", nil)];
            });
        } else {
            NSLog(@"Block upload error: %@, %ld", errorResult, (long)code);
            
        }
    } andProgress:^(NSInteger bytesWritten, NSInteger totalBytesWritten, NSInteger totalBytesExpectedToWrite, id context) {
        NSLog(@"Block upload progress: %ld/%ld (+%ld)", (long)totalBytesWritten, (long)totalBytesExpectedToWrite, (long)bytesWritten);
    }];
    
}

- (void)didSignapUser:(User *) user {
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
