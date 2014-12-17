//
//  ClubImagesPageViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/15/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubImagesPageViewController.h"
#import "UIImageView+WebCache.h"
#import "SDWebImageManager.h"

@interface ClubImagesPageViewController ()

@end

@implementation ClubImagesPageViewController
{
    NSMutableArray* photos;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.imageView.indicatorDisabled = YES;
    
    photos = [[NSMutableArray alloc] init];
    for (int i = 0; i < [self.place.photos count]; i++) {

        NSURL* url = [NSURL URLWithString:[self.place.photos objectAtIndex:i]];
        
        [[SDWebImageManager sharedManager] downloadImageWithURL:url options:0 progress:^(NSInteger receivedSize, NSInteger expectedSize)
         {}
                                                      completed:^(UIImage *image, NSError* error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL)
         {
             if (image)
             {
                 [photos addObject:image];
                 [self.imageView reloadData];
             }
         }];
    }
    self.imageView.delegate = self;
    self.imageView.dataSource = self;
}

- (void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    self.imageView.pageControl.currentPageIndicatorTintColor = [UIColor colorWithRed:0.705 green:0.238 blue:0.791 alpha:1.000];
    self.imageView.pageControl.pageIndicatorTintColor = [UIColor whiteColor];
    self.imageView.pageControl.center = CGPointMake(CGRectGetWidth(self.imageView.frame) / 2, CGRectGetHeight(self.imageView.frame) - 27);
    
}

- (NSArray *) arrayWithImages
{
    return [[NSArray alloc] initWithArray: photos];
}

- (UIViewContentMode) contentModeForImage:(NSUInteger)image
{
    return UIViewContentModeScaleAspectFill;
}

- (UIImage *) placeHolderImageForImagePager {
    return [UIImage imageNamed:@"Default"];
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
