//
//  ClubFooterView.h
//  Clubbook
//
//  Created by Andrew on 7/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface ClubFooterView : UICollectionReusableView
@property (weak, nonatomic) IBOutlet UILabel *clubUsersLabel;
@property (weak, nonatomic) IBOutlet UILabel *checkinLabel;
@property (weak, nonatomic) IBOutlet UITextView *usersLeftToCheckinLabel;
@property (weak, nonatomic) IBOutlet UIView *footerContainer;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *loadingIndicator;
@property (weak, nonatomic) IBOutlet UILabel *footerInfoLabel;

- (void)collapse;
- (void)expand;
@end
