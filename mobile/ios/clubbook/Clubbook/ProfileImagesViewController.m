//
//  ProfileImagesViewController.m
//  Clubbook
//
//  Created by Andrew on 10/27/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ProfileImagesViewController.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "ChatViewController.h"
#import "UIButton+WebCache.h"
#import "UIView+StringTagAdditions.h"
#import "CSNotificationView.h"
#import "LocationHelper.h"
#import "TransitionFromUserToClubUsers.h"
#import "ClubUsersViewController.h"
#import "ClubUsersYesterdayViewController.h"
#import "UserNearbyViewController.h"

@interface ProfileImagesViewController (){
    // User *_user;
    float oldX;
}


@end

@implementation ProfileImagesViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = self.user.name;
    // Do any additional setup after loading the view.
    
    
    [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"profile" ascending:NO];
    
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    
    NSArray *sortedPhotos = [self.user.photos  sortedArrayUsingDescriptors:sortDescriptors];
    
    for (int i = 0; i < [sortedPhotos count]; i++) {
        CGRect frame;
        frame.origin.x = self.imageScrollView.frame.size.width * i;
        frame.origin.y = 0;
        frame.size = self.imageScrollView.frame.size;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        //imageView.contentMode = UIViewContentModeCenter;
        /*if (imageView.bounds.size.width > ((UIImage*)imagesArray[i]).size.width && imageView.bounds.size.height > ((UIImage*)imagesArray[i]).size.height) {
            imageView.contentMode = UIViewContentModeScaleAspectFit;
        }*/
        
        imageView.backgroundColor = [UIColor blackColor];
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @450, @"height": @450, @"crop": @"fit"}];
        //c_fit,h_450,w_450/v140483
        NSString *avatarUrl = [cloudinary url: [[sortedPhotos objectAtIndex:i] valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        
        [imageView sd_setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty.png"]];
        //[imageView setBackgroundColor:[UIColor whiteColor]];
        [self.imageScrollView addSubview:imageView];
        
    }
    
    self.imageScrollView.delegate = self;
    
    self.imagePageControl.numberOfPages = self.imageScrollView.subviews.count;
    
    self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [self.user.photos count]  , self.imageScrollView.frame.size.height + 2);

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
  /*  [self.navigationController.navigationBar setBackgroundImage:[UIImage new]
                                                  forBarMetrics:UIBarMetricsDefault];
    self.navigationController.navigationBar.shadowImage = [UIImage new];
    self.navigationController.navigationBar.translucent = YES;
    //self.navigationController.view.backgroundColor = [UIColor colorWithRed:52/255.0 green:3/255.0 blue:69/255.0 alpha:1];
    self.navigationController.navigationBar.backgroundColor= [UIColor colorWithRed:52/255.0 green:3/255.0 blue:69/255.0 alpha:1];*/

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"User Images Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
   /// self.navigationController.navigationBar.translucent = NO;
    
  /*  [self.navigationBar setBackgroundImage:[UIImage new]
                             forBarMetrics:UIBarMetricsDefault];
    self.navigationBar.shadowImage = [UIImage new];
    self.navigationBar.translucent = YES;*/


}

#pragma mark - UIScrollView Delegate
- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    if (sender == self.imageScrollView) {
        [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
        
        // Update the page when more than 50% of the previous/next page is visible
        CGFloat pageWidth = self.imageScrollView.frame.size.width;
        int page = floor((self.imageScrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
        self.imagePageControl.currentPage = page;
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

- (IBAction)backAction:(id)sender {
}
@end
