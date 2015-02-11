//
//  TextViewController.h
//  Clubbook
//
//  Created by Andrew on 6/30/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseTableViewController.h"
#import "CbButton.h"
#import "Place.h"

@interface ClubViewController : BaseTableViewController
//@property (weak, nonatomic) IBOutlet CbButton *checkinButton;

@property (strong, nonatomic) Place *place;
@property (weak, nonatomic) IBOutlet UILabel *clubDescLabel;
@property (weak, nonatomic) IBOutlet UILabel *ageRestrictionLabel;
@property (weak, nonatomic) IBOutlet UILabel *capacityLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UIButton *addressButton;
@property (weak, nonatomic) IBOutlet UILabel *dressCodeLabel;
@property (weak, nonatomic) IBOutlet UIButton *phoneButton;
@property (weak, nonatomic) IBOutlet UIButton *siteButton;
@property (weak, nonatomic) IBOutlet UIButton *emailButton;
@property (weak, nonatomic) IBOutlet UILabel *monTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *monHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *tueTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *tueHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *wedTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *wedHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *thoTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *thoHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *friTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *friHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *satTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *satHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *sanTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *sanHoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *distanceLabel;
- (IBAction)distanceAction:(id)sender;
- (IBAction)phoneAction:(id)sender;
- (IBAction)siteAction:(id)sender;
- (IBAction)emailAction:(id)sender;

@end
