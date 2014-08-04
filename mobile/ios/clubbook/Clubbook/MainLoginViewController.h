//
//  MainLoginViewController.h
//  Clubbook
//
//  Created by Andrew on 6/17/14.
//  Copyright (c) 2014 Appcoda. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseViewController.h"
#import "Cloudinary.h"


@interface MainLoginViewController : BaseViewController

@property (weak, nonatomic) IBOutlet UILabel *sloganLabel;
@property (weak, nonatomic) IBOutlet UIButton *fbButton;
@property (weak, nonatomic) IBOutlet UIButton *regButton;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
//@property (weak, nonatomic) IBOutlet TTTAttributedLabel *termsOfUserLabel;
@property (weak, nonatomic) IBOutlet UILabel *termOfUseLabel;
@property (weak, nonatomic) IBOutlet UIButton *termOfUseButton;
- (IBAction)fbAction:(id)sender;
@end
