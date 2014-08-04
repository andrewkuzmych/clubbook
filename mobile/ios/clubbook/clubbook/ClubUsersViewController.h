//
//  ClubUsersViewController.h
//  Clubbook
//
//  Created by Andrew on 7/28/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "CbButton.h"

@interface ClubUsersViewController : BaseViewController <UICollectionViewDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *profileCollection;
@property (strong, nonatomic) NSString *placeId;
- (IBAction)checkinAction:(CbButton *)sender;

@end
