//
//  ProfileViewController.h
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseTableViewController.h"
#import "CLUploader.h"

@interface ProfileViewController : BaseTableViewController <UIPickerViewDelegate, UIScrollViewDelegate, UIActionSheetDelegate, UIPickerViewDataSource, UIPickerViewDelegate, CLUploaderDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>
@property (weak, nonatomic) IBOutlet UITextField *countryText;
@property (weak, nonatomic) IBOutlet UIScrollView *photosScroll;
@property (weak, nonatomic) IBOutlet UIImageView *topImage;
@property (weak, nonatomic) IBOutlet UIButton *setDefaultButton;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
@property (weak, nonatomic) IBOutlet UIButton *addPhotoButton;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *genderText;
@property (weak, nonatomic) IBOutlet UITextField *bdayText;
@property (weak, nonatomic) IBOutlet UITextField *bioText;
- (IBAction)addPhotoAction:(id)sender;
- (IBAction)setAsDefaultAction:(id)sender;
- (IBAction)deleteAction:(id)sender;
- (IBAction)saveAction:(id)sender;


@end
