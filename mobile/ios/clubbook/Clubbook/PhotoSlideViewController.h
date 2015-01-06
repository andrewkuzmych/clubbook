//
//  PhotoSlideViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "BaseViewController.h"

@interface PhotoSlideViewController : BaseViewController<UIScrollViewDelegate>

@property (weak, nonatomic) IBOutlet UIScrollView *imageScrollView;
@property (weak, nonatomic) IBOutlet UIPageControl *imagePageControl;

@property (strong, nonatomic) NSMutableArray* photosArrays;
@property NSUInteger selectedIndex;

@end
