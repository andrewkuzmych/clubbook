//
//  ClubUsersYesterdayViewController.h
//  Clubbook
//
//  Created by Andrew on 10/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "CbButton.h"
#import "ProfileCell.h"
#import "User.h"
#import "HeaderView.h"
#import "ClubFooterView.h"

@interface ClubUsersYesterdayViewController : BaseViewController<UICollectionViewDataSource, UICollectionViewDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *profileCollection;
@property (strong, nonatomic) NSString *placeId;
@property (strong, nonatomic) Place *place;
@property (assign, nonatomic) BOOL hasBack;
@property (nonatomic, strong) HeaderView *headerView;
@property (nonatomic, strong) ClubFooterView *clubFooterView;


- (ProfileCell*)collectionViewCellForThing:(User *)user;
@end
