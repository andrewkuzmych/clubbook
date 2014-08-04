//
//  BaseViewControllerHelper.h
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Reachability.h"

@interface BaseViewControllerHelper : NSObject
{
    Reachability *internetReachableFoo;
}

@property (strong, nonatomic) UIActivityIndicatorView *spinner;
@property (strong, nonatomic) UIView *loadingView;

- (void)showProgress: (BOOL) clearContext title:(NSString*) title;
- (void)hideProgress;
- (void)testInternetConnection;
- (void)showError;

//- (id) initWithController:(UIViewController *)controller;
- (id)initBase:(UIViewController *)controller sidebarButton:(UIBarButtonItem *)sidebarButton;

@end
