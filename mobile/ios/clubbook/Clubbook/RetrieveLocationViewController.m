//
//  RetrieveLocationViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/9/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "RetrieveLocationViewController.h"
#import "LocationManagerSingleton.h"
#import "CLCloudinary.h"
#import "CLTransformation.h"
#import "Constants.h"

@interface RetrieveLocationViewController ()

@end

@implementation RetrieveLocationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSDictionary *userAvatar = [defaults objectForKey:@"userAvatar"];
    
    // transform avatar
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLTransformation *transformation = [CLTransformation transformation];
    [transformation setParams: @{@"width": @100, @"height": @100, @"crop": @"thumb", @"gravity": @"face"}];
    
    NSString * avatarUrl  = [cloudinary url: [userAvatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];

    UIImage* placeholderImage = [UIImage imageNamed:@"avatar_empty.png"];
    NSData* imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:avatarUrl]];
    UIImage* avatarImage = [UIImage imageWithData:imageData];
    
    if(avatarImage) {
        self.photoImageView.image = avatarImage;
    }
    else {
        self.photoImageView.image = placeholderImage;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];

    [self startToLocate];
    
    [self performSegueWithIdentifier: @"onLocation" sender: self];
}

- (void) startToLocate {
    if([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied) {
        NSString* warningMessage = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Allow Clubbook to use your location", nil)];
        [self.infoLabel setText:warningMessage];
        [self.activityIndicator stopAnimating];
        
        [self.retryButton setHidden:NO];
    }
    else {
        [self.retryButton setHidden:YES];
        NSString* warningMessage = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Checking your location...", nil)];
        [self.infoLabel setText:warningMessage];
        [self.activityIndicator startAnimating];
        
        [LocationManagerSingleton sharedSingleton].delegate = self;
        [[LocationManagerSingleton sharedSingleton] startLocating];
    }
}

- (void)noLocation {
}

- (void)yesLocation
{
    [self performSegueWithIdentifier: @"onLocation" sender: self];
}


- (void)didUpdateLocation
{
    [self yesLocation];
}

- (void)didFailUpdateLocation
{
    [self noLocation];
}
- (IBAction)handleRetry:(id)sender {
    [self startToLocate];
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
