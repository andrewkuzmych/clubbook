//
//  SidebarViewController.h
//  SidebarDemo
//
//  Created by Simon on 29/6/13.
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "BaseViewController.h"
#import "PubNub.h"

@interface SidebarViewController : BaseViewController <UICollectionViewDelegate, UICollectionViewDataSource, PNDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *gradientImageView;
@property (weak, nonatomic) IBOutlet UICollectionView *menuCollectionView;
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImageView;
@end
