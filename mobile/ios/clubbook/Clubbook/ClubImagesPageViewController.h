//
//  ClubImagesPageViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QMBParallaxScrollViewController.h"
#import "KIImagePager.h"

#import "Place.h"

@interface ClubImagesPageViewController : UIViewController <KIImagePagerDelegate, KIImagePagerDataSource>

@property (weak, nonatomic) IBOutlet KIImagePager *imageView;
@property (weak, nonatomic) IBOutlet UIImageView *placeAvatar;
@property (strong, nonatomic) Place *place;
@end
