//
//  ClubViewParallaxControllerViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubViewParallaxControllerViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface ClubViewParallaxControllerViewController ()

@end

@implementation ClubViewParallaxControllerViewController
{
    NSString* accessToken;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.clubImages = [self.storyboard instantiateViewControllerWithIdentifier:@"ClubImagesPageViewController"];
    self.clubView = [self.storyboard instantiateViewControllerWithIdentifier:@"ClubViewController"];
    
    self.clubView.place = self.place;
    self.clubImages.place = self.place;
    
    self.delegate = self;
    
    [self setupWithTopViewController:self.clubImages andTopHeight:200 andBottomViewController:self.clubView];
    
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    [self.followButton setMainState:@"Follow"];
    [self.followButton setSecondState:@"UnFollow"];
    
    [self.view insertSubview:self.followButton aboveSubview:self.clubView.view];
    
    [self setMaxHeight:400.0];
    
    NSUserDefaults* userDefaults = [NSUserDefaults standardUserDefaults];
    accessToken = [userDefaults objectForKey:@"accessToken"];
    
    //check if place is followed
    if (self.place.isFavorite) {
        //[self.followButton setS
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - QMBParallaxScrollViewControllerDelegate

- (void)parallaxScrollViewController:(QMBParallaxScrollViewController *)controller didChangeState:(QMBParallaxState)state{
    if (self.state == QMBParallaxStateFullSize){
        [self.navigationController setNavigationBarHidden:YES animated:YES];
    } else {
        [self.navigationController setNavigationBarHidden:NO animated:YES];
        
    }
}

- (void) viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
}

- (IBAction)handleFollowButton:(id)sender {
    if (self.followButton.statusOn) {
        self.place.isFavorite = YES;
        [self._manager makePlaceFavorite:self.place.id accessToken:accessToken makeFavorite:YES];
    }
    else {
        self.place.isFavorite = NO;
        [self._manager makePlaceFavorite:self.place.id accessToken:accessToken makeFavorite:NO];
    }
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
