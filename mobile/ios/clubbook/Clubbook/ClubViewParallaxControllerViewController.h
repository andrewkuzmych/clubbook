//
//  ClubViewParallaxControllerViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "QMBParallaxScrollViewController.h"
#import "ClubImagesPageViewController.h"
#import "ClubViewController.h"

@interface ClubViewParallaxControllerViewController : QMBParallaxScrollViewController <QMBParallaxScrollViewControllerDelegate>

@property (weak, nonatomic) ClubImagesPageViewController* clubImages;
@property (weak, nonatomic) ClubViewController* clubView;

@property (strong, nonatomic) Place* place;

@end
