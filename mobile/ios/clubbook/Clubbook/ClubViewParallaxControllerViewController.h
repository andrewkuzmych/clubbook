//
//  ClubViewParallaxControllerViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "QMBParallaxScrollViewController.h"
#import "ClubImagesPageViewController.h"
#import "InfoViewController.h"
#import "TMFloatingButton.h"
#import "DJ.h"

@interface ClubViewParallaxControllerViewController : QMBParallaxScrollViewController <QMBParallaxScrollViewControllerDelegate>

@property (weak, nonatomic) ClubImagesPageViewController* clubImages;
@property (weak, nonatomic) InfoViewController* clubView;

@property (strong, nonatomic) TMFloatingButton *followButton;
@property (strong, nonatomic) Place* place;
@property (strong, nonatomic) DJ* dj;

@end
