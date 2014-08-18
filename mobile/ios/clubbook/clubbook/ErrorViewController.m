//
//  ErrorViewController.m
//  Clubbook
//
//  Created by Andrew on 7/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ErrorViewController.h"

@interface ErrorViewController ()

@end

@implementation ErrorViewController



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
    // Do any additional setup after loading the view.
    self.noInternetLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:20.0];
    self.retryButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:16.0];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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

- (IBAction)retryAction:(id)sender {
    
    UINavigationController *purchaseContr = (UINavigationController *)[self.storyboard instantiateViewControllerWithIdentifier:@"main"];
    //menu is only an example
    purchaseContr.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
    

    [self presentViewController:purchaseContr animated:NO completion:nil];

}
@end
