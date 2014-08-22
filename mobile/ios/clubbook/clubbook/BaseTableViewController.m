//
//  BaseTableViewController.m
//  Clubbook
//
//  Created by Andrew on 6/21/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseTableViewController.h"
#import "User.h"
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Cloudinary.h"
#import "ErrorViewController.h"
#import "SWRevealViewController.h"
#import "BaseViewControllerHelper.h"
#import "CSNotificationView.h"
//UITableViewDataSource and UITableViewDelegate

@interface BaseTableViewController ()<ClubbookManagerDelegate, UITableViewDelegate > {
    BaseViewControllerHelper *baseViewControllerHelper;
}

@end

@implementation BaseTableViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    self.currentFocus = textField;
    //self.scrollView.contentOffset = point;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Set the side bar button action. When it's tapped, it'll show up the sidebar.
    baseViewControllerHelper = [[BaseViewControllerHelper alloc] initBase:self sidebarButton:_sidebarButton];
    
    //[baseViewControllerHelper testInternetConnection];
    
    self._manager = [[ClubbookManager alloc] init];
    self._manager.communicator = [[ClubbookCommunicator alloc] init];
    self._manager.communicator.delegate = self._manager;
    self._manager.delegate = self;
    
      self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

- (void)failedWithError:(NSError *)error
{
    [baseViewControllerHelper showError];
    
   /* ErrorViewController *purchaseContr = (ErrorViewController *)[self.storyboard instantiateViewControllerWithIdentifier:@"error"];
    //menu is only an example
    purchaseContr.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
    [self presentViewController:purchaseContr animated:NO completion:nil];
    */
}

- (UIToolbar *)generateToolbarForKeyboard
{
    NSArray *segItemsArray = [NSArray arrayWithObjects: @"Previous", @"Next", nil];
    UISegmentedControl *segmentedControl = [[UISegmentedControl alloc] initWithItems:segItemsArray];
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:12], UITextAttributeFont, nil];
    [segmentedControl setTitleTextAttributes:textAttributes forState:UIControlStateNormal];
    
    segmentedControl.frame = CGRectMake(0, 0, 130, 10);
    segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
    segmentedControl.selectedSegmentIndex = -1;
    [segmentedControl addTarget:self
                         action:@selector(segmentAction:)
               forControlEvents:UIControlEventValueChanged];
    
    
    UIFont *font = [UIFont boldSystemFontOfSize:10.0f];
    NSDictionary *attributes = [NSDictionary dictionaryWithObject:font
                                                           forKey:NSFontAttributeName];
    [segmentedControl setTitleTextAttributes:attributes
                                    forState:UIControlStateNormal];
    
    
    UIToolbar* baseToolbar = [[UIToolbar alloc]initWithFrame:CGRectMake(0, 0, 320, 20)];
    
    
    UIBarButtonItem * done = [[UIBarButtonItem alloc]initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(doneWithNumberPad)];
    
    [done setTitleTextAttributes:@{NSFontAttributeName: [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:16.0]} forState:UIControlStateNormal];
    
    baseToolbar.items = [NSArray arrayWithObjects:
                         [[UIBarButtonItem alloc] initWithCustomView:(UIView *)segmentedControl],
                         [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil],
                         done,
                         nil];
    [baseToolbar sizeToFit];
    return baseToolbar;
}

-(void)doneWithNumberPad{
    [self.view endEditing:YES];
}

- (void)viewWillDisappear:(BOOL)animated {
    [self doneWithNumberPad];
}


#pragma mark - Segment delegate stuff

- (void)segmentAction:(id)sender {
    
    UISegmentedControl *segmentedControl = (UISegmentedControl *) sender;
    
    NSInteger step = -1;
    if (segmentedControl.selectedSegmentIndex == 1)
        step = 1;
    
    NSInteger nextTag = self.currentFocus.tag + step;
    // Try to find next responder
    UIResponder* nextResponder = [self.view viewWithTag:nextTag];
    if (nextResponder) {
        // Found next responder, so set it.
        [nextResponder becomeFirstResponder];
    } else {
        // Not found, so remove keyboard.
        [self.currentFocus resignFirstResponder];
    }
    segmentedControl.selectedSegmentIndex = -1;
    
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


-(void)showAlert: (NSString *) message title:(NSString *) title {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title
                                                    message:message
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
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
