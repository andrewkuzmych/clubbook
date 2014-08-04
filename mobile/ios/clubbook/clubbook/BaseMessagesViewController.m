//
//  BaseMessagesViewController.m
//  Clubbook
//
//  Created by Andrew on 7/8/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseMessagesViewController.h"
#import "User.h"
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Cloudinary.h"
#import "BaseViewControllerHelper.h"
#import "ErrorViewController.h"
#import "CSNotificationView.h"

@interface BaseMessagesViewController ()<ClubbookManagerDelegate> {

    UIView *loadingView;
    BaseViewControllerHelper *baseViewControllerHelper;

}
@end

@implementation BaseMessagesViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Set the side bar button action. When it's tapped, it'll show up the sidebar.
    baseViewControllerHelper = [[BaseViewControllerHelper alloc] initBase:self sidebarButton:nil];
    
    [baseViewControllerHelper testInternetConnection];
    
    self._manager = [[ClubbookManager alloc] init];
    self._manager.communicator = [[ClubbookCommunicator alloc] init];
    self._manager.communicator.delegate = self._manager;
    self._manager.delegate = self;
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
    /*ErrorViewController *purchaseContr = (ErrorViewController *)[self.storyboard instantiateViewControllerWithIdentifier:@"error"];
    purchaseContr.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
    [self presentViewController:purchaseContr animated:NO completion:nil];*/
    
    [baseViewControllerHelper showError];

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
