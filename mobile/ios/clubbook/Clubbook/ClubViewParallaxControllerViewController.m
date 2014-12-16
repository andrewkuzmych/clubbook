//
//  ClubViewParallaxControllerViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubViewParallaxControllerViewController.h"

@interface ClubViewParallaxControllerViewController ()

@end

@implementation ClubViewParallaxControllerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.clubImages = [self.storyboard instantiateViewControllerWithIdentifier:@"ClubImagesPageViewController"];
    self.clubView = [self.storyboard instantiateViewControllerWithIdentifier:@"ClubViewController"];
    
    self.clubView.place = self.place;
    self.clubImages.place = self.place;
    
    self.title = self.place.title;
    
    self.delegate = self;
    
    [self setupWithTopViewController:self.clubImages andTopHeight:200 andBottomViewController:self.clubView];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - QMBParallaxScrollViewControllerDelegate

- (void)parallaxScrollViewController:(QMBParallaxScrollViewController *)controller didChangeState:(QMBParallaxState)state{
    if (self.state == QMBParallaxStateFullSize){
        [self.navigationController setNavigationBarHidden:YES animated:YES];
        
    }else {
        [self.navigationController setNavigationBarHidden:NO animated:YES];
        
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
