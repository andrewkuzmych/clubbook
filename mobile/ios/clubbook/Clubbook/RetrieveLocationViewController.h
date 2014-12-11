//
//  RetrieveLocationViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/9/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LocationManagerProtocol.h"
#import "MultiplePulsingHaloLayer.h"

@interface RetrieveLocationViewController : UIViewController <LocationManagerProtocol>
@property (weak, nonatomic) IBOutlet UIImageView *photoImageView;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UIButton *retryButton;
@property (nonatomic, strong) MultiplePulsingHaloLayer *halo;
@property (weak, nonatomic) IBOutlet UIView *testView;


@end
