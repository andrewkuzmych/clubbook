//
//  CustomNavigationController.m
//  Clubbook
//
//  Created by Andrew on 10/11/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "CustomNavigationController.h"
#import "MessagesViewController.h"
#import "ChatViewController.h"
#import "FriendsViewController.h"
#import "AppDelegate.h"
#import "GlobalVars.h"

@interface CustomNavigationController ()

@end

@implementation CustomNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    AppDelegate *ad = ((AppDelegate *)[UIApplication sharedApplication].delegate);
    ad.mainNC = self;
    // Do any additional setup after loading the view.
    
    
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];

    if([GlobalVars getInstance].PushNavigationPage == 1) {
        //MessagesViewController *controller = (MessagesViewController*)[mainStoryboard instantiateViewControllerWithIdentifier: @"messages"];
        ChatViewController *controller = (ChatViewController*)[mainStoryboard instantiateViewControllerWithIdentifier: @"chat"];
        controller.userTo = [GlobalVars getInstance].ChatUserId;
        //[GlobalVars getInstance].ChatUserId = chatUserId;
        [GlobalVars getInstance].PushNavigationPage = 0;
        [self pushViewController:controller animated:YES];
    } else if ([GlobalVars getInstance].PushNavigationPage == 2) {
        FriendsViewController *controller = (FriendsViewController*)[mainStoryboard instantiateViewControllerWithIdentifier: @"friends"];
        [GlobalVars getInstance].PushNavigationPage = 0;
        [self pushViewController:controller animated:YES];
    }

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(BOOL)shouldAutorotate
{
    return [[self.viewControllers lastObject] shouldAutorotate];
}

-(NSUInteger)supportedInterfaceOrientations
{
    UIViewController *aa = [self.viewControllers lastObject];
    
    NSString *t = aa.title;
    return [[self.viewControllers lastObject] supportedInterfaceOrientations];
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation
{
    return [[self.viewControllers lastObject] preferredInterfaceOrientationForPresentation];
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
