//
//  BaseViewControllerHelper.m
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewControllerHelper.h"
#import "SWRevealViewController.h"
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "MRProgress.h"
#import "CSNotificationView.h"

@implementation BaseViewControllerHelper{
    UIViewController* currentController;
    CSNotificationView* permanentNotification;
}



/*- (id) initWithController:(UIViewController *)controller{
    self = [super init];
    if (self) {
        currentController = controller;
    }
    return self;
}*/

- (id)initBase:(UIViewController *)controller sidebarButton:(UIBarButtonItem *)sidebarButton
{
    self = [super init];
    if (self) {
        currentController = controller;
        // Set the side bar button action. When it's tapped, it'll show up the sidebar.
        if (sidebarButton != nil) {
            sidebarButton.target = currentController.revealViewController;
            sidebarButton.action = @selector(revealToggle:);
            [currentController.view addGestureRecognizer:currentController.revealViewController.panGestureRecognizer];
        }
    
        [currentController.navigationController.navigationBar setTitleTextAttributes:
         [NSDictionary dictionaryWithObjectsAndKeys:
          [UIColor whiteColor], UITextAttributeTextColor,
          [UIFont fontWithName:@"TitilliumWeb-Bold" size:17], NSFontAttributeName, nil]];
    }
    return self;
}

// Checks if we have an internet connection or not
- (void)testInternetConnection
{
    internetReachableFoo = [Reachability reachabilityWithHostname:@"www.google.com"];
    
    // Internet is reachable
    internetReachableFoo.reachableBlock = ^(Reachability*reach)
    {
        // Update the UI on the main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            NSLog(@"Yayyy, we have the interwebs!");
        });
    };
    
    // Internet is not reachable
    internetReachableFoo.unreachableBlock = ^(Reachability*reach)
    {
        // Update the UI on the main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            [self showError];
            NSLog(@"Someone broke the internet :(");
        });
    };
    
    [internetReachableFoo startNotifier];
}

- (void)showError
{
    [CSNotificationView showInViewController:currentController
                                       style:CSNotificationViewStyleError
                                     message:@"No Internet Connection."];

}

-(void)showProgress: (BOOL) clearContext title:(NSString*) title
{
    self.loadingView = [[UIView alloc] initWithFrame:currentController.view.bounds];
    
    if(clearContext) {
        [self.loadingView setBackgroundColor: [UIColor colorWithPatternImage: [UIImage imageNamed:@"background.png"]]];
        
    }
    else {
        [self.loadingView setBackgroundColor: [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.5]];
    }

    if (title == nil) {
        // show spinner
        self.spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        CGFloat screenWidth = screenRect.size.width;
        CGFloat screenHeight = screenRect.size.height;
        [self.spinner setCenter:CGPointMake(screenWidth/2, screenHeight/2)];
        self.spinner.hidesWhenStopped = YES;
        
        [self.loadingView addSubview:self.spinner];
        
        [currentController.view addSubview:self.loadingView];
        
        [self.spinner startAnimating];
        self.spinner.hidden = NO;
    } else{
       // show loading under navigation bar
        permanentNotification =
        [CSNotificationView notificationViewWithParentViewController:currentController.navigationController
                                                           tintColor:[UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1]
                                                               image:nil message:title];
        
        [permanentNotification setShowingActivity:YES];
        
        [permanentNotification setVisible:YES animated:YES completion:^{}];
    }
}

-(void)hideProgress
{
    if (permanentNotification!=nil) {
        [permanentNotification dismiss];
        permanentNotification = nil;
        
    }
    
    [self.loadingView removeFromSuperview];
    [MRProgressOverlayView dismissOverlayForView:currentController.navigationController.view animated:YES];
    [self.spinner stopAnimating];
    self.spinner.hidden = YES;
}
@end
