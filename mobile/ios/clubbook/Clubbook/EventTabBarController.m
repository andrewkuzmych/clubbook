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
#import "NewsFeedViewController.h"

@interface EventTabBarController ()

@end

@implementation EventTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSMutableArray* arrayOfControllers = [[NSMutableArray alloc] init];
    NSMutableDictionary* controllersWithNames = [[NSMutableDictionary alloc] init];
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    for (UIViewController *v in self.viewControllers) {
        if ([v isKindOfClass:[EventDetailsViewController class]]) {
            EventDetailsViewController* controller = (EventDetailsViewController*)v;
            controller.event = self.event;
            controller.title = @"Event details";
            [controllersWithNames setValue:controller forKey:@"details"];
        }
        if ([v isKindOfClass:[EventsViewController class]]) {
            EventsViewController* controller = (EventsViewController*)v;
            controller.place = self.event.place;
            controller.dj = self.event.dj;
            controller.title = @"All Events";
            [controllersWithNames setValue:controller forKey:@"events"];
        }
        if ([v isKindOfClass:[NewsFeedViewController class]]) {
            NewsFeedViewController* controller = (NewsFeedViewController*)v;
            controller.title = @"All News";
            
            if (self.event.place) {
                controller.type = self.event.place.category;
                controller.newsObjectId = self.event.place.id;
            }
            else if (self.event.dj) {
                controller.type = @"dj";
                controller.newsObjectId = self.event.dj.djId;
            }
            
            [controllersWithNames setValue:controller forKey:@"news"];
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
    [arrayOfControllers addObject:[controllersWithNames objectForKey:@"news"]];
    
    [self setViewControllers:arrayOfControllers];

    self.title = self.event.title;
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
