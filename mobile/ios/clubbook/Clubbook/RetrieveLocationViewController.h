//
//  RetrieveLocationViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/9/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LocationManagerProtocol.h"

@interface RetrieveLocationViewController : UIViewController <LocationManagerProtocol>
@property (weak, nonatomic) IBOutlet UIImageView *photoImageView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UIButton *retryButton;

@end
