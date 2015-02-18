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
    
    if (self.place != nil) {
        [self.clubView fillWithPlaceData:self.place];
        self.clubImages.photos = self.place.photos;
        self.clubImages.avatar = self.place.avatar;
    }
    
    if (self.dj != nil) {
        [self.clubView fillWithDJData:self.dj];
        self.clubImages.photos = self.dj.photos;
        self.clubImages.avatar = self.dj.avatar;
    }
    
    self.delegate = self;
    
    [self setupWithTopViewController:self.clubImages andTopHeight:200 andBottomViewController:self.clubView];
    
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    self.followButton = [[TMFloatingButton alloc] initWithWidth:60.0f withMargin:8.0f andPosition:FloatingButtonPositionBottomRight andHideDirection:FloatingButtonHideDirectionUp andSuperView:self.view];
    TMFloatingButtonState* stateOn = [[TMFloatingButtonState alloc] initWithText:@"Follow" andBackgroundColor:[UIColor colorWithRed:0.000 green:0.698 blue:0.000 alpha:1.000] forButton:self.followButton];
    TMFloatingButtonState* stateOff = [[TMFloatingButtonState alloc] initWithText:@"UnFollow" andBackgroundColor:[UIColor colorWithRed:0.913 green:0.131 blue:0.029 alpha:1.000] forButton:self.followButton];
    
    [self.followButton addTarget:self action:@selector(handleFollowButton) forControlEvents:UIControlEventTouchUpInside];
    
    if (self.place.isFavorite) {
        [self.followButton addState:stateOn forName:@"stateOn"];
        [self.followButton addAndApplyState:stateOff forName:@"stateOff"];
    }
    else {
        [self.followButton addAndApplyState:stateOn forName:@"stateOn"];
        [self.followButton addState:stateOff forName:@"stateOff"];
    }

    
    [self.view insertSubview:self.followButton aboveSubview:self.clubView.view];
    
    [self setMaxHeight:400.0];
    
    NSUserDefaults* userDefaults = [NSUserDefaults standardUserDefaults];
    accessToken = [userDefaults objectForKey:@"accessToken"];
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

- (void) handleFollowButton {
    if (!self.place.isFavorite) {
        self.place.isFavorite = YES;
        [self._manager makePlaceFavorite:self.place.id accessToken:accessToken makeFavorite:YES];
        [self.followButton setButtonState:@"stateOff"];
    }
    else {
        self.place.isFavorite = NO;
        [self._manager makePlaceFavorite:self.place.id accessToken:accessToken makeFavorite:NO];
        [self.followButton setButtonState:@"stateOn"];
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
