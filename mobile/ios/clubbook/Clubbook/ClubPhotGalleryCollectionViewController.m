//
//  ClubPhotGalleryCollectionViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "ClubPhotGalleryCollectionViewController.h"
#import "ClubPhotoGalleryCollectionViewCell.h"
#import "PhotoSlideViewController.h"
#import "SDWebImageManager.h"
#import "EBPhotoPagesController.h"

@interface ClubPhotGalleryCollectionViewController ()

@end

@implementation ClubPhotGalleryCollectionViewController
{
    NSInteger selectedItem;
}

static NSString * const reuseIdentifier = @"PhotoCell";

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.photoArray = [[NSMutableArray alloc] init];
    for (int i = 0; i < [self.place.photos count]; i++) {
        
        NSURL* url = [NSURL URLWithString:[self.place.photos objectAtIndex:i]];
        
        [[SDWebImageManager sharedManager] downloadImageWithURL:url options:0 progress:^(NSInteger receivedSize, NSInteger expectedSize)
         {}
                                                      completed:^(UIImage *image, NSError* error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL)
         {
             if (image)
             {
                 [self.photoArray addObject:image];
             }
         }];
    }
    selectedItem = 0;

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark <UICollectionViewDataSource>

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return [self.photoArray count];
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ClubPhotoGalleryCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:reuseIdentifier forIndexPath:indexPath];
    
    UIImage* image = [self.photoArray objectAtIndex:indexPath.item];
    if (image != nil) {
        cell.clubPhotoImageView.contentMode = UIViewContentModeScaleAspectFill;
        [cell.clubPhotoImageView setImage:image];
    }
    // Configure the cell
    
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    selectedItem = indexPath.item;
    EBPhotoPagesController *photoPagesController = [[EBPhotoPagesController alloc]
                                                    initWithDataSource:self delegate:self photoAtIndex:selectedItem];
    
    [self presentViewController:photoPagesController animated:YES completion:nil];
}

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"onPhoto"]) {
        PhotoSlideViewController *photoController =  [segue destinationViewController];
        photoController.photosArrays = self.photoArray;
        photoController.title = self.place.title;
        photoController.selectedIndex = selectedItem;
        }
}

#pragma mark - Image Datasource methods
- (UIImage *)photoPagesController:(EBPhotoPagesController *)controller
                     imageAtIndex:(NSInteger)index {
    return [self.photoArray objectAtIndex:index];
}

- (void)photoPagesController:(EBPhotoPagesController *)controller
                imageAtIndex:(NSInteger)index
           completionHandler:(void(^)(UIImage *image))handler {
    
}

- (BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController
    shouldExpectPhotoAtIndex:(NSInteger)index {
    if (index < [self.photoArray count]) {
       return YES;
    }
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowCommentingForPhotoAtIndex:(NSInteger)index {
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowReportForPhotoAtIndex:(NSInteger)index {
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowMiscActionsForPhotoAtIndex:(NSInteger)index {
    return NO;
}
@end
