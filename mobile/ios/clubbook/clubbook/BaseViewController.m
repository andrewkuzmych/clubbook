//
//  BaseViewController.m
//  Clubbook
//
//  Created by Andrew on 6/19/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "User.h"
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Cloudinary.h"
#import "SWRevealViewController.h"
#import "ErrorViewController.h"
#import "CSNotificationView.h"

@interface BaseViewController ()<ClubbookManagerDelegate> {
    BaseViewControllerHelper *baseViewControllerHelper;
}
@end

@implementation BaseViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
       
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Set the side bar button action. When it's tapped, it'll show up the sidebar.
     baseViewControllerHelper = [[BaseViewControllerHelper alloc] initBase:self sidebarButton:_sidebarButton];
    
    [baseViewControllerHelper testInternetConnection];
    
    self._manager = [[ClubbookManager alloc] init];
    self._manager.communicator = [[ClubbookCommunicator alloc] init];
    self._manager.communicator.delegate = self._manager;
    self._manager.delegate = self;
    self.revealViewController.delegate = self;
    [self.revealViewController.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)showProgress: (BOOL) clearContext title:(NSString*) title
{
    [baseViewControllerHelper showProgress:clearContext title:title];
}

-(void)hideProgress
{
    [baseViewControllerHelper hideProgress];
}

- (void)failedWithError:(NSError *)error
{
    [baseViewControllerHelper showError];
   // [CSNotificationView showInViewController:self
     //                                  style:CSNotificationViewStyleSuccess
      //                               message:@"User information saved."];

/*    ErrorViewController *purchaseContr = (ErrorViewController *)[self.storyboard instantiateViewControllerWithIdentifier:@"error"];
    purchaseContr.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
    [self presentViewController:purchaseContr animated:NO completion:nil];
*/
}

- (void)revealController:(SWRevealViewController *)revealController didMoveToPosition:(FrontViewPosition)position
{
    self.view.userInteractionEnabled = (revealController.frontViewPosition == FrontViewPositionRight ? FALSE : TRUE);
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
