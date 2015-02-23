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
#import "BaseViewController.h"

@interface RetrieveLocationViewController : BaseViewController <LocationManagerProtocol>
@property (weak, nonatomic) IBOutlet UIImageView *photoImageView;
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UIButton *retryButton;
@property (nonatomic, strong) MultiplePulsingHaloLayer *halo;

@end
