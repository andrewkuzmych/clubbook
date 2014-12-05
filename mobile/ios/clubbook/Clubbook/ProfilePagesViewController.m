//
//  ProfilePagesViewController.m
//  Clubbook
//
//  Created by Andrew on 10/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ProfilePagesViewController.h"
#import "UserProfileViewController.h"
#import "SwipeView.h"
#import "User.h"

@interface ProfilePagesViewController ()<SwipeViewDelegate, SwipeViewDataSource>
{
    NSMutableArray *eventsControllersArray;
    SwipeView *swipeView;
}
@end


@implementation ProfilePagesViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Chat";
    self.pageController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    
    self.pageController.dataSource = self;
    [[self.pageController view] setFrame:[[self view] bounds]];
    
    UserProfileViewController *initialViewController = [self viewControllerAtIndex:self.index];
    
    NSArray *viewControllers = [NSArray arrayWithObject:initialViewController];
    
    [self.pageController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
    
    [self addChildViewController:self.pageController];
    [[self view] addSubview:[self.pageController view]];
    [self.pageController didMoveToParentViewController:self];
    
    self.edgesForExtendedLayout = UIRectEdgeNone; 
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

   // self.navigationController.navigationBar.translucent = NO;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //self.navigationController.navigationBar.translucent = YES;
}

- (UserProfileViewController *)viewControllerAtIndex:(NSUInteger)index {
    
    //UserProfileViewController *childViewController = [[UserProfileViewController alloc] initWithNibName:@"user_profile" bundle:nil];
    
    UserProfileViewController *childViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"user_profile"];
    
    childViewController.user =  [self.profiles objectAtIndex:index];
    childViewController.currentPlace = self.currentPlace;
    
    self.title = @"User Profile";
    /*if (childViewController.user.isFriend) {
        self.title = @"Friend";
    }*/

    childViewController.index = index;
    
    return childViewController;
    
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController {
    
    NSUInteger index = [(UserProfileViewController *)viewController index];
    
    if (index == 0) {
        //return nil;
        index = self.profiles.count;
    }
    
    // Decrease the index by 1 to return
    index--;
    
    return [self viewControllerAtIndex:index];
    
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController {
    
    NSUInteger index = [(UserProfileViewController *)viewController index];
    
    index++;
    
    if (index == self.profiles.count) {
        index = 0;
//        return nil;
    }
    
    return [self viewControllerAtIndex:index];
    
}

/*
- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController {
    // The number of items reflected in the page indicator.
    return self.profiles.count;
}

- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController {
    // The selected item reflected in the page indicator.
    return 0;
}*/

/*
-(void)createEventsVuewConttrolls
{
    eventsControllersArray = [[NSMutableArray alloc]initWithCapacity:[_profiles count]];
    //NSMutableArray *eventsControllersArrayMut = [[NSMutableArray alloc]initWithCapacity:[_profiles count]];
    for(User *profile in _profiles)
    {
        UserProfileViewController *userProfileViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"user_profile"];
        userProfileViewController.user = profile;
        //userProfileViewController.navigationController = self.navigationController;
        [eventsControllersArray addObject:userProfileViewController];
    }
    //eventsControllersArray = [eventsControllersArrayMut copy];
}

- (void)dealloc
{
    swipeView.delegate = nil;
    swipeView.dataSource = nil;
}

- (NSInteger)numberOfItemsInSwipeView:(SwipeView *)swipeView
{
    return [eventsControllersArray count];
}

- (UIView *)swipeView:(SwipeView *)_swipeView viewForItemAtIndex:(NSInteger)index reusingView:(UIView *)view
{
    view = [[eventsControllersArray objectAtIndex:index]view];
    [[eventsControllersArray objectAtIndex:index] viewDidAppear:YES];
    [[eventsControllersArray objectAtIndex:index] viewWillAppear:YES];
    [view setFrame:CGRectMake(0, 0, _swipeView.bounds.size.width, _swipeView.bounds.size.height)];
    return view;
}
*/
@end
