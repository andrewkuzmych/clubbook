//
//  TextViewController.m
//  Clubbook
//
//  Created by Andrew on 6/30/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubViewController.h"
#import "ProfileCell.h"
#import "HeaderView.h"
#import "ClubFooterView.h"
#import "UIImageView+WebCache.h"
#import "Place.h"
#import "GlobalVars.h"
#import "LocationHelper.h"
#import "UserViewController.h"
#import "Constants.h"
#import "LocationManagerSingleton.h"
#import "WorkingHour.h"
#import <MapKit/MapKit.h>

@interface ClubViewController (){
    BOOL showAllUsers;
    int collapsedUserCount;
}

@property (nonatomic, strong) HeaderView *headerView;
@property (nonatomic, strong) ClubFooterView *clubFooterView;

@end

@implementation ClubViewController

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
    
    self.title = self.place.title;
    
    [self populateData];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Club Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}


- (void)populateData
{    
    if (self.place.address!= nil) {
        [self.addressLabel setText:self.place.address];
    } else {
        self.addressLabel.text = NSLocalizedString(@"unknown", nil);
    }
    
    if (self.place.site!= nil) {
        [self.siteButton setTitle:self.place.site forState:UIControlStateNormal];
    } else {
        [self.siteButton setTitle:NSLocalizedString(@"unknown", nil) forState:UIControlStateNormal];
    }
    
    if (self.place.email!= nil) {
        [self.emailButton setTitle:self.place.email forState:UIControlStateNormal];
    } else {
        [self.emailButton setTitle:NSLocalizedString(@"unknown", nil) forState:UIControlStateNormal];
    }
    
    if (self.place.phone!= nil) {
        [self.phoneButton setTitle:self.place.phone forState:UIControlStateNormal];
    } else {
        [self.phoneButton setTitle:NSLocalizedString(@"unknown", nil) forState:UIControlStateNormal];
    }
    
    if (self.place.capacity!= 0) {
        self.capacityLabel.text =  [NSString stringWithFormat:@"%d",self.place.capacity];
    } else {
        self.capacityLabel.text =  NSLocalizedString(@"unknown", nil);
    }
    
    if (self.place.ageRestriction!= 0) {
        self.ageRestrictionLabel.text = self.place.ageRestriction;
    } else {
        self.ageRestrictionLabel.text =  NSLocalizedString(@"zero_plus", nil);
    }
    
    if (self.place.dressCode!= 0) {
        self.dressCodeLabel.text = self.place.dressCode;
    } else {
        self.dressCodeLabel.text = NSLocalizedString(@"none", nil);
    }
    
    int disatanceInt = (int)self.place.distance;
    self.distanceLabel.text = [LocationHelper convertDistance:disatanceInt];
    
    self.clubDescLabel.text = self.place.info;
    
    self.monHoursLabel.text = NSLocalizedString(@"unknown", nil);
    self.tueHoursLabel.text = NSLocalizedString(@"unknown", nil);
    self.wedHoursLabel.text = NSLocalizedString(@"unknown", nil);
    self.thoHoursLabel.text = NSLocalizedString(@"unknown", nil);
    self.friHoursLabel.text = NSLocalizedString(@"unknown", nil);
    self.satHoursLabel.text = NSLocalizedString(@"unknown", nil);
    self.sanHoursLabel.text = NSLocalizedString(@"unknown", nil);
    
    for (WorkingHour *workingHour in self.place.workingHours) {
        switch (workingHour.day) {
            case 1:
                [self setWorkingHoursText:workingHour label:self.monHoursLabel];
                break;
            case 2:
                [self setWorkingHoursText:workingHour label:self.tueHoursLabel];
                break;
            case 3:
                [self setWorkingHoursText:workingHour label:self.wedHoursLabel];
                break;
            case 4:
                [self setWorkingHoursText:workingHour label:self.thoHoursLabel];
                break;
            case 5:
                [self setWorkingHoursText:workingHour label:self.friHoursLabel];
                break;
            case 6:
                [self setWorkingHoursText:workingHour label:self.satHoursLabel];
                break;
            case 0:
                [self setWorkingHoursText:workingHour label:self.sanHoursLabel];
                break;
            default:
                break;
        }
    }
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setWorkingHoursText:(WorkingHour *)workingHour label:(UILabel *) label
{
    if ([workingHour.status isEqualToString:@"opened"] ) {
        label.text = [NSString stringWithFormat:@"%@ - %@",workingHour.startTime, workingHour.endTime];
    } else
        label.text = NSLocalizedString(@"closed", nil);
    
    if (workingHour.day == self.place.todayWorkingHours.day) {
        label.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:13.0];
        [label setTextColor:[UIColor colorWithRed:0.000 green:0.571 blue:0.000 alpha:1.000]];
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell *cell = [self tableView:tableView cellForRowAtIndexPath:indexPath];
    float height = cell.frame.size.height;
    
    /*if (indexPath.row == 0) {
        height = 140.0f;
    }
    else*/ if (indexPath.row == 5) {
        CGSize maximumLabelSize = CGSizeMake(280,9999);
        
        UILabel *gettingSizeLabel = [[UILabel alloc] init];
        gettingSizeLabel.font = self.clubDescLabel.font;
        gettingSizeLabel.text = self.clubDescLabel.text;
        gettingSizeLabel.numberOfLines = self.clubDescLabel.numberOfLines;
        gettingSizeLabel.lineBreakMode = self.clubDescLabel.lineBreakMode;
        
        CGSize expectedLabelSize = [gettingSizeLabel sizeThatFits:maximumLabelSize];
        
        //adjust the label the the new height.
        CGRect newFrame = self.clubDescLabel.frame;
        newFrame.size.height = expectedLabelSize.height;
        self.clubDescLabel.frame = newFrame;
        height = newFrame.origin.y + expectedLabelSize.height + 10;
    
        [self.clubDescLabel sizeToFit];
    }
    
    return height;
}

- (IBAction)distanceAction:(id)sender {
    MKPlacemark* place = [[MKPlacemark alloc] initWithCoordinate: CLLocationCoordinate2DMake([self.place.lat doubleValue], [self.place.lon doubleValue]) addressDictionary: nil];
    MKMapItem* destination = [[MKMapItem alloc] initWithPlacemark: place];
    destination.name = self.place.title;
    NSArray* items = [[NSArray alloc] initWithObjects: destination, nil];
    NSDictionary* options = [[NSDictionary alloc] initWithObjectsAndKeys:
                             MKLaunchOptionsDirectionsModeWalking,
                             MKLaunchOptionsDirectionsModeKey, nil];
    [MKMapItem openMapsWithItems: items launchOptions: options];
}

- (IBAction)addressAction:(id)sender {
    MKPlacemark* place = [[MKPlacemark alloc] initWithCoordinate: CLLocationCoordinate2DMake([self.place.lat doubleValue], [self.place.lon doubleValue]) addressDictionary: nil];
    MKMapItem* destination = [[MKMapItem alloc] initWithPlacemark: place];
    destination.name = self.place.title;
    NSArray* items = [[NSArray alloc] initWithObjects: destination, nil];
    NSDictionary* options = [[NSDictionary alloc] initWithObjectsAndKeys:
                             MKLaunchOptionsDirectionsModeWalking,
                             MKLaunchOptionsDirectionsModeKey, nil];
    [MKMapItem openMapsWithItems: items launchOptions: options];
}

- (IBAction)phoneAction:(id)sender {
    UIButton * button = (UIButton*)sender;
    if ([button.titleLabel.text isEqualToString:NSLocalizedString(@"unknown", nil)]) {
        return;
    }
    UIDevice *device = [UIDevice currentDevice];
    if ([[device model] isEqualToString:@"iPhone"] ) {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"tel:%@",button.titleLabel.text]]];
    }
}

- (IBAction)siteAction:(id)sender {
     UIButton * button = (UIButton*)sender;
    if ([button.titleLabel.text isEqualToString:NSLocalizedString(@"unknown", nil)]) {
        return;
    }
     [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@",button.titleLabel.text]]];
}

- (IBAction)emailAction:(id)sender {
    UIButton * button = (UIButton*)sender;
    if ([button.titleLabel.text isEqualToString:NSLocalizedString(@"unknown", nil)]) {
        return;
    }
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"mailto://%@",button.titleLabel.text]]];
}
@end
