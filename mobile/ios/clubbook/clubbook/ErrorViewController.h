//
//  ErrorViewController.h
//  Clubbook
//
//  Created by Andrew on 7/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ErrorViewController : UIViewController
@property (weak, nonatomic) IBOutlet UILabel *noInternetLabel;
@property (weak, nonatomic) IBOutlet UIButton *retryButton;

- (IBAction)retryAction:(id)sender;

@end
