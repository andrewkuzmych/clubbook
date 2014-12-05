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
    float oldX;
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

    [self styleUi];
    
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
    self.nameLabel.text = NSLocalizedString(@"about", nil); //self.place.title;
    
    if (self.place.address!= nil) {
        self.addressLabel.text = self.place.address;
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
    
    
    [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    for (int i = 0; i < [self.place.photos count]; i++) {
        CGRect frame;
        frame.origin.x = self.imageScrollView.frame.size.width * i;
        frame.origin.y = 0;
        frame.size = self.imageScrollView.frame.size;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        imageView.backgroundColor = [UIColor blackColor];
        
        [imageView setImageWithURL:[NSURL URLWithString:[self.place.photos objectAtIndex:i]] placeholderImage:[UIImage imageNamed:@"Default.png"]];
        [self.imageScrollView addSubview:imageView];
    }
    //int count =  self.imageScrollView.subviews.count;
    self.imageScrollView.delegate = self;
    
    self.imagePageView.numberOfPages = self.imageScrollView.subviews.count;
    
    self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [self.place.photos count]  , self.imageScrollView.frame.size.height + 5);
}

- (void)styleUi
{
    self.nameLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:19.0];
    self.clubDescLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    self.ageRestrictionTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    self.ageRestrictionTitleLabel.text = NSLocalizedString(@"ageRestiction", nil);
    
    self.dressCodeTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    self.dressCodeTitleLabel.text = NSLocalizedString(@"dressCode", nil);
    
    self.capacityTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    self.capacityTitleLabel.text = NSLocalizedString(@"capacity", nil);
    
    self.ageRestrictionLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:14.0];
    
    self.dressCodeLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15.0];
    
    self.capacityLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15.0];

    
    self.addressLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    self.distanceLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:11.0];
    self.addressButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    
    self.siteButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    
    self.emailButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    
    self.phoneButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    
    UIFont *workingHoursFont = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    self.monTitleLabel.font = workingHoursFont;
    self.monHoursLabel.font = workingHoursFont;
    self.tueTitleLabel.font = workingHoursFont;
    self.tueHoursLabel.font = workingHoursFont;
    self.wedTitleLabel.font = workingHoursFont;
    self.wedHoursLabel.font = workingHoursFont;
    self.thoTitleLabel.font = workingHoursFont;
    self.thoHoursLabel.font = workingHoursFont;
    self.friTitleLabel.font = workingHoursFont;
    self.friHoursLabel.font = workingHoursFont;
    self.satTitleLabel.font = workingHoursFont;
    self.satHoursLabel.font = workingHoursFont;
    self.sanTitleLabel.font = workingHoursFont;
    self.sanHoursLabel.font = workingHoursFont;

    self.monTitleLabel.text = NSLocalizedString(@"mon", nil);
    self.tueTitleLabel.text = NSLocalizedString(@"tue", nil);
    self.wedTitleLabel.text = NSLocalizedString(@"wed", nil);
    self.thoTitleLabel.text = NSLocalizedString(@"tho", nil);
    self.friTitleLabel.text = NSLocalizedString(@"fri", nil);
    self.satTitleLabel.text = NSLocalizedString(@"sat", nil);
    self.sanTitleLabel.text = NSLocalizedString(@"san", nil);

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
    }
}


- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
    
    // Update the page when more than 50% of the previous/next page is visible
    CGFloat pageWidth = self.imageScrollView.frame.size.width;
    int page = floor((self.imageScrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.imagePageView.currentPage = page;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell *cell = [self tableView:tableView cellForRowAtIndexPath:indexPath];
    float height = cell.frame.size.height;
    
    
    if (indexPath.row == 1) {
        CGSize maximumLabelSize = CGSizeMake(280,9999);
        CGSize expectedLabelSize = [self.clubDescLabel.text sizeWithFont:self.clubDescLabel.font
                                        constrainedToSize:maximumLabelSize
                                            lineBreakMode:self.clubDescLabel.lineBreakMode];
        
        
        
        //adjust the label the the new height.
        CGRect newFrame = self.clubDescLabel.frame;
        newFrame.size.height = expectedLabelSize.height;
        self.clubDescLabel.frame = newFrame;
        height = newFrame.origin.y + expectedLabelSize.height;
    
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
