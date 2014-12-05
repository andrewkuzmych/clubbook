//
//  ProfileImagesViewController.h
//  Clubbook
//
//  Created by Andrew on 10/27/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseViewController.h"

@interface ProfileImagesViewController : BaseViewController<UIScrollViewDelegate>
@property (weak, nonatomic) IBOutlet UIScrollView *imageScrollView;
@property (weak, nonatomic) User *user;
@property (weak, nonatomic) IBOutlet UIPageControl *imagePageControl;
@property (weak, nonatomic) IBOutlet UINavigationBar *navigationBar;
- (IBAction)backAction:(id)sender;

@end
