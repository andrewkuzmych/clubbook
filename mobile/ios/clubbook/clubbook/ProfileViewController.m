//
//  ProfileViewController.m
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ProfileViewController.h"
#import "UIImageView+WebCache.h"
#import "UIButton+WebCache.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "Convertor.h"
#import "UIView+StringTagAdditions.h"
#import "Validator.h"
#import "SessionHelper.h"
#import "CSNotificationView.h"

@interface ProfileViewController () {
        User *_user;
        float oldX;
}

@property (strong, nonatomic) UIDatePicker *datePicker;
@property (strong, nonatomic) NSMutableArray *uploadedImages;
@property (strong, nonatomic) UIPickerView *countryPicker;
@property (strong, nonatomic) UIPickerView *genderPicker;
@property (strong, nonatomic) NSArray *countryPickerArray;
@property (strong, nonatomic) NSArray *genderPickerArray;
@property (strong, nonatomic) UIButton *currentImageButton;
@property(nonatomic,retain) NSString* dobString;
@property(nonatomic,retain) NSString* gender;
@end

@implementation ProfileViewController

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
    
    self.uploadedImages = [[NSMutableArray alloc] init];
    
    [self renderUi];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [self showProgress:YES title:nil];
    [self._manager retrieveUser:userId];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)renderUi
{
    UIToolbar *baseToolbar = [self generateToolbarForKeyboard];
    
    UIColor *placeholderColor = [UIColor colorWithRed:200/255.0 green:200/255.0 blue:200/255.0 alpha:1];
    
    self.nameText.inputAccessoryView = baseToolbar;
    self.nameText.delegate = self;
    self.nameText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"first_name", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:@"TitilliumWeb-Regular" size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.bioText.inputAccessoryView = baseToolbar;
    self.bioText.delegate = self;
    self.bioText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"bio", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:@"TitilliumWeb-Regular" size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    
    self.datePicker   = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 210, 320, 216)];
    [self.datePicker setDatePickerMode:UIDatePickerModeDate];
    self.datePicker.backgroundColor = [UIColor whiteColor];
    [self.datePicker addTarget:self action:@selector(dboChanged:) forControlEvents:UIControlEventValueChanged];
    
    self.bdayText.inputView  = self.datePicker;
    self.bdayText.delegate = self;
    self.bdayText.inputAccessoryView = baseToolbar;
    self.bdayText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"birthday", nil) attributes:@{NSFontAttributeName : [UIFont fontWithName:@"TitilliumWeb-Regular" size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.genderPickerArray = [[NSArray alloc] initWithObjects:NSLocalizedString(@"male", nil), NSLocalizedString(@"female", nil), nil];
    self.genderPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    
    self.genderPicker.delegate = self;
    self.genderPicker.dataSource = self;
    
    self.genderText.inputView = self.genderPicker;
    self.genderText.text = NSLocalizedString(@"birthday", nil);
    self.gender = @"male";
    self.genderText.delegate = self;
    self.genderText.inputAccessoryView = baseToolbar;
    self.genderText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Gender" attributes:@{ NSFontAttributeName : [UIFont fontWithName:@"TitilliumWeb-Regular" size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.countryPickerArray = [NSArray arrayWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Countries" ofType:@"plist"]];
    self.countryPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    
    self.countryPicker.delegate = self;
    self.countryPicker.dataSource = self;
    
    
    self.countryText.inputView = self.countryPicker;
    //self.gender = @"male";
    self.countryText.delegate = self;
    self.countryText.inputAccessoryView = baseToolbar;
    self.countryText.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Country" attributes:@{ NSFontAttributeName : [UIFont fontWithName:@"TitilliumWeb-Regular" size:16.0], NSForegroundColorAttributeName: placeholderColor}];
    
    self.saveButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:16];
    self.setDefaultButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:11];
    self.deleteButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:11];
    
    
    [self.photosScroll.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
}

- (void)didUpdateUserImage:(User *)user
{
    [self hideProgress];
    [self populateImageCollection:user isProfileDefoult:YES];
    [SessionHelper StoreUser: user];
}

- (void)didDeleteUserImage:(User *)user
{
    [self hideProgress];
    [self populateImageCollection:user isProfileDefoult:YES];
}

- (void)didUpdateUser:(User *) user
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleSuccess
                                         message:@"User information saved."];
                
        [SessionHelper StoreUser: user];
        
        //[self performSegueWithIdentifier: @"onLogin" sender: self];
    });
    
}

- (void)didReceiveUser:(User *)user
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        self.nameText.text = user.name;
        
        self.bioText.text = user.bio;
        
        self.genderText.text = NSLocalizedString(user.gender, nil);
        if ([user.gender isEqualToString:@"male"]) {
            [self.genderPicker selectRow:0 inComponent:0 animated:YES];
        } else {
            [self.genderPicker selectRow:1 inComponent:0 animated:YES];
        }
        
        self.countryText.text = [user.country capitalizedString];
        
        NSInteger countryIndex = [self getCountryIndex:user.country];
        [self.countryPicker selectRow:countryIndex inComponent:0 animated:YES];
        
        [self setDob:user.dob];
        
        [self populateImageCollection:user isProfileDefoult:NO];
        
    });
}

- (void)didAddUserImage:(User *)user
{
    [self hideProgress];
    [self populateImageCollection:user isProfileDefoult:NO];
}

- (void)actionSheet:(UIActionSheet *)popup clickedButtonAtIndex:(NSInteger)buttonIndex
{
            switch (buttonIndex) {
                case 0:
                {
                    // click yes
                    [self showProgress:NO title:nil];
                    NSString *photoId = self.currentImageButton.stringTag;
                    [self._manager deleteUserImage:_user.id objectId:photoId];
                    break;
                }
                case 1:
                    // click cancel
                    break;
                default:
                    break;
            }

}

- (void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage* avatar = (UIImage *) [info objectForKey:UIImagePickerControllerEditedImage];
    [self dismissViewControllerAnimated:YES completion:^{
        // Do something with the image
    }];
    
    // upload image to coudinary
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLUploader* uploader = [[CLUploader alloc] init:cloudinary delegate:self];
    
    NSData *avatarData = UIImageJPEGRepresentation(avatar, 1.0);
    [self showProgress:NO title:NSLocalizedString(@"loading", nil)];//loading
    [uploader upload:avatarData options:@{} withCompletion:^(NSDictionary *successResult, NSString *errorResult, NSInteger code, id context) {
        if (successResult) {
            dispatch_async(dispatch_get_main_queue(), ^{
                NSString *avatarResult = [Convertor convertDictionaryToJsonString:successResult];
                [self._manager addUserImage:_user.id avatar:avatarResult];
            });
        } else {
            [self hideProgress];
            NSLog(@"Block upload error: %@, %ld", errorResult, (long)code);
            
        }
    } andProgress:^(NSInteger bytesWritten, NSInteger totalBytesWritten, NSInteger totalBytesExpectedToWrite, id context) {
        NSLog(@"Block upload progress: %ld/%ld (+%ld)", (long)totalBytesWritten, (long)totalBytesExpectedToWrite, (long)bytesWritten);
    }];
}

- (void)setDob:(NSDate *)date
{
    if (date != nil) {
        NSDateFormatter *format = [[NSDateFormatter alloc] init];
        [format setDateFormat:@"dd MMM yyyy"];
        NSString* finalDateString = [format stringFromDate:date];
        
        NSDateFormatter *shortFormat = [[NSDateFormatter alloc] init];
        [shortFormat setDateFormat:@"dd.MM.yyyy"];
        
        _dobString = [shortFormat stringFromDate:date];
        
        [self.datePicker setDate:date animated:YES];
        
        self.bdayText.text=finalDateString;
    }
}

-(void)dboChanged:(id)sender
{
    NSDate *dateToTExtField=[_datePicker date];
   
    [self setDob:dateToTExtField];
}

- (UIButton *)setupImageButton:(CGFloat)originX
{
    CGRect frame;
    frame.origin.x = originX;
    frame.origin.y = 0;
    frame.size.height = self.photosScroll.frame.size.height;
    frame.size.width = self.photosScroll.frame.size.height;
    
    UIButton *imageButton = [UIButton buttonWithType:UIButtonTypeCustom];
    imageButton.frame = frame;
    
    [imageButton addTarget:self action:@selector(changeSelectedImage:)
          forControlEvents:UIControlEventTouchUpInside];
    imageButton.imageView.contentMode = UIViewContentModeScaleAspectFill;
    return imageButton;
}

- (void)populateImageCollection:(User *) user isProfileDefoult:(BOOL) isProfileDefoult
{
    _user = user;
    NSMutableArray *reversedArray = [[user.photos reverseObjectEnumerator] allObjects];
    
    // remove all subviews
    [[self.photosScroll subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    for (int i = 0; i < [reversedArray count]; i++) {
        CGFloat originX = self.photosScroll.frame.size.height*(i +[self.uploadedImages count]);

        UIButton *imageButton = [self setupImageButton:originX];
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @600}];
        
        NSString *avatarUrl = [cloudinary url: [[reversedArray objectAtIndex:i] valueForKey:@"public_id"] options:@{@"transformation": transformation}];

        [imageButton setImageWithURL:[NSURL URLWithString:avatarUrl] forState:UIControlStateNormal];
        NSString* photoId = [[reversedArray objectAtIndex:i] valueForKey:@"_id"];
        imageButton.stringTag = photoId;
    
        
        NSNumber * isProfileNumber = (NSNumber *)[[reversedArray objectAtIndex:i] valueForKey:@"profile"];
        imageButton.boolTag = (isProfileNumber && [isProfileNumber boolValue] == YES);
        
        [self.photosScroll addSubview:imageButton];

        // set selected image
        if (isProfileDefoult) {
            if (imageButton.boolTag) {
                [self changeSelectedImage:imageButton];
                [self.topImage setImageWithURL:[NSURL URLWithString:avatarUrl]];
            }
        } else
            if (i==0) {
                [self changeSelectedImage:imageButton];
                [self.topImage setImageWithURL:[NSURL URLWithString:avatarUrl]];
            }
    }
    
    self.photosScroll.delegate = self;
    
    self.photosScroll.contentSize = CGSizeMake(self.photosScroll.frame.size.height * ([user.photos count] + [self.uploadedImages count]) + 5  , self.photosScroll.frame.size.height + 5);
}

-(NSInteger) getCountryIndex:(NSString *) country
{
    for (int i=0; i<self.countryPickerArray.count; i++) {
        
        NSString *name = [[self.countryPickerArray objectAtIndex:i] objectForKey:@"name"];
        if([[country lowercaseString] isEqualToString:[name lowercaseString]])
        {
            return i;
        }
    }
    return 0;
}

- (void)changeSelectedImage:(UIButton*)button
{
    self.topImage.image = button.imageView.image;
    self.setDefaultButton.hidden = button.boolTag;
    self.deleteButton.hidden = button.boolTag || _user.photos.count == 1;
    self.currentImageButton = button;
}

#pragma mark - Actions

- (IBAction)addPhotoAction:(id)sender
{
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

- (IBAction)setAsDefaultAction:(id)sender
{
    [self showProgress:NO title:nil];
    NSString *photoId = self.currentImageButton.stringTag;
    [self._manager updateUserImage:_user.id objectId:photoId];
}

- (IBAction)deleteAction:(id)sender
{
    UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSure", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                            NSLocalizedString(@"yes", nil),
                            nil];
    [popup showInView:[UIApplication sharedApplication].keyWindow];
}

- (IBAction)saveAction:(id)sender
{
    
    NSString *wrongName = [NSString stringWithFormat:NSLocalizedString(@"wrongName", nil)];
    NSString *wrongDob = [NSString stringWithFormat:NSLocalizedString(@"wrongDob", nil)];
    NSString *wrongGender = [NSString stringWithFormat:NSLocalizedString(@"wrongGender", nil)];
    NSString *wrongCountry = [NSString stringWithFormat:NSLocalizedString(@"wrongCountry", nil)];
    
    
    BOOL isValidName = [Validator NSStringLength:self.nameText.text:2];
    if(isValidName == NO)
    {
        [CSNotificationView showInViewController:self
                                           style:CSNotificationViewStyleError
                                         message:wrongName];
        return;
    }
    
    BOOL isValidGender = [Validator NSStringLength:self.genderText.text:4];
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
    
    [self showProgress:NO title:nil];
    [self._manager updateUser:_user.id name:self.nameText.text gender:self.gender dob:_dobString country:self.countryText.text bio:self.bioText.text];
    
}

#pragma mark - Scroll stuff
- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    [self.photosScroll setContentOffset: CGPointMake(self.photosScroll.contentOffset.x, oldX  )];
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

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if([pickerView isEqual: self.countryPicker]){
        self.countryText.text = [[self.countryPickerArray objectAtIndex:row] objectForKey:@"name"];
    }else if([pickerView isEqual: self.genderPicker]){
        self.gender = @"male";
        if (row == 1) {
            self.gender = @"female";
        }
        
        self.genderText.text = [self.genderPickerArray objectAtIndex:row];

    }
}

@end
