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
#import "FloatingButton.h"

@interface ClubViewParallaxControllerViewController : QMBParallaxScrollViewController <QMBParallaxScrollViewControllerDelegate>

@property (weak, nonatomic) ClubImagesPageViewController* clubImages;
@property (weak, nonatomic) ClubViewController* clubView;
@property (weak, nonatomic) IBOutlet FloatingButton *followButton;

@property (strong, nonatomic) Place* place;

@end
