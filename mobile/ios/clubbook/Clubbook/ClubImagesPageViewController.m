//
//  ClubImagesPageViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubImagesPageViewController.h"

@interface ClubImagesPageViewController ()

@end

@implementation ClubImagesPageViewController
{
    NSMutableArray* photos;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    photos = [[NSMutableArray alloc] init];
    
    for (int i = 0; i < [self.place.photos count]; i++) {
        UIImage* placeholderImage = [UIImage imageNamed:@"Default.png"];
        NSData* imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:[self.place.photos objectAtIndex:i]]];
        UIImage* image = [UIImage imageWithData:imageData];
        
        if(image) {
            [photos addObject:image];
        }
        else {
            [photos addObject:placeholderImage];
        }
        
   
    }
    //int count =  self.imageScrollView.subviews.count;
    self.imageView.delegate = self;
    self.imageView.dataSource = self;
}

- (void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    self.imageView.pageControl.currentPageIndicatorTintColor = [UIColor colorWithRed:0.705 green:0.238 blue:0.791 alpha:1.000];
    self.imageView.pageControl.pageIndicatorTintColor = [UIColor whiteColor];
    self.imageView.pageControl.center = CGPointMake(CGRectGetWidth(self.imageView.frame) / 2, CGRectGetHeight(self.imageView.frame) - 27);
    
    self.imageView.indicatorDisabled = YES;
}

- (NSArray *) arrayWithImages
{
    return [[NSArray alloc] initWithArray: photos];
}

- (UIViewContentMode) contentModeForImage:(NSUInteger)image
{
    return UIViewContentModeScaleAspectFill;
}



/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
