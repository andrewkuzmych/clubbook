//
//  EventTabBartController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/18/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "EventTabBarController.h"
#import "EventsViewController.h"
#import "EventDetailsViewController.h"
#import "ClubViewParallaxControllerViewController.h"

@interface EventTabBarController ()

@end

@implementation EventTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSMutableArray* arrayOfControllers = [[NSMutableArray alloc] init];
    NSMutableDictionary* controllersWithNames = [[NSMutableDictionary alloc] init];
    
    for (UIViewController *v in self.viewControllers) {
        if ([v isKindOfClass:[EventDetailsViewController class]]) {
            EventDetailsViewController* controller = (EventDetailsViewController*)v;
            controller.event = self.event;
            controller.title = @"Event details";
            [controllersWithNames setValue:controller forKey:@"details"];
        }
        if ([v isKindOfClass:[EventsViewController class]]) {
            EventsViewController* controller = (EventsViewController*)v;
            controller.event = self.event;
            controller.title = @"All Event";
            [controllersWithNames setValue:controller forKey:@"events"];
        }
    }
    
    [[UITabBar appearance] setBarTintColor:[UIColor whiteColor]];
    
    UIStoryboard* storyBoard = [UIStoryboard storyboardWithName:@"ClubProfileStoryboard" bundle:nil];
    ClubViewParallaxControllerViewController* paralax = [storyBoard instantiateViewControllerWithIdentifier:@"parallax"];
    paralax.place = self.event.place;
    paralax.dj = self.event.dj;
    paralax.title = @"Profile";
    [controllersWithNames setValue:paralax forKey:@"profile"];
    
    [arrayOfControllers addObject:[controllersWithNames objectForKey:@"details"]];
    [arrayOfControllers addObject:[controllersWithNames objectForKey:@"profile"]];
    [arrayOfControllers addObject:[controllersWithNames objectForKey:@"events"]];
    
    
    [self setViewControllers:arrayOfControllers];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
