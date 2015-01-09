//
//  RegViewController.h
//  Clubbook
//
//  Created by Andrew on 6/20/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseTableViewController.h"
#import "CLUploader.h"

@interface RegViewController : BaseTableViewController  <UIPickerViewDelegate, UIPickerViewDataSource, UINavigationControllerDelegate, UIImagePickerControllerDelegate, CLUploaderDelegate>

@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *genderName;
@property (weak, nonatomic) IBOutlet UITextField *emailText;
@property (weak, nonatomic) IBOutlet UITextField *passText;
@property (weak, nonatomic) IBOutlet UIButton *regButton;
@property (weak, nonatomic) IBOutlet UITextField *dobText;
@property (weak, nonatomic) IBOutlet UITextField *countryText;
- (IBAction)addProfileImage:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *profileImage;
- (IBAction)registerAction:(id)sender;
@end
