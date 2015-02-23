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
#import "MultiplePulsingHaloLayer.h"
#import "LocationHelper.h"

@interface RetrieveLocationViewController ()

@end

@implementation RetrieveLocationViewController
{
    
    BOOL locationReceived;
    BOOL userReceiver;

}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    
    locationReceived = NO;
    userReceiver = NO;
    
    ///setup single halo layer
    MultiplePulsingHaloLayer *multiLayer = [[MultiplePulsingHaloLayer alloc] initWithHaloLayerNum:3 andStartInterval:1];
    self.halo = multiLayer;
    self.halo.position = self.photoImageView.center;
    self.halo.useTimingFunction = NO;
    [self.halo buildSublayers];
    self.halo.radius = 150;
    self.halo.fromValueForRadius = 0.3;
    [self.halo setHaloLayerColor:[UIColor whiteColor].CGColor];
    [self.view.layer insertSublayer:self.halo below:self.photoImageView.layer];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSDictionary *userAvatar = [defaults objectForKey:@"userAvatar"];

    NSString* accessToken = [defaults objectForKey:@"accessToken"];
    [self._manager retrieveUser:accessToken];

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
}

- (void) viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    if(self.halo) {
        self.halo.position = self.photoImageView.center;
    }
}

- (void) startToLocate {
    if([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied) {
        NSString* warningMessage = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Allow Clubbook to use your location", nil)];
        [self.infoLabel setText:warningMessage];
        [self.retryButton setHidden:NO];
        [self.halo setHidden:YES];
    }
    else {
        [self.retryButton setHidden:YES];
        [self.halo setHidden:NO];
        NSString* warningMessage = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Checking your location...", nil)];
        [self.infoLabel setText:warningMessage];

        [LocationManagerSingleton sharedSingleton].delegate = self;
        [[LocationManagerSingleton sharedSingleton] startLocating];
    }
}

- (void)noLocation {
}

- (void)yesLocation
{
    if (locationReceived && userReceiver) {
        [self performSegueWithIdentifier: @"onLocation" sender: self];
    }

}


- (void)didUpdateLocation
{
    locationReceived = YES;
    [self yesLocation];
}

- (void)didFailUpdateLocation
{
    [self noLocation];
}
- (IBAction)handleRetry:(id)sender {
    [self startToLocate];
}

- (void)didReceiveUser:(User *)user
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        LocationHelper* helper = [LocationHelper sharedInstance];
        helper.placeId = user.currentCheckinClubName;
        userReceiver = YES;
        [self yesLocation];
    });
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
