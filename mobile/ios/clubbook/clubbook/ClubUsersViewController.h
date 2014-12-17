//
//  ClubUsersViewController.h
//  Clubbook
//
//  Created by Andrew on 7/28/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "HeaderView.h"
#import "ClubFooterView.h"
#import "User.h"
#import "ProfileCell.h"
#import "BaseViewController.h"
#import "CbButton.h"

@interface ClubUsersViewController : BaseViewController <UICollectionViewDelegate, UICollectionViewDataSource, UIAlertViewDelegate, PNDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *profileCollection;
@property (strong, nonatomic) NSString *placeId;
@property (strong, nonatomic) Place *place;
@property (assign, nonatomic) BOOL hasBack;
@property (nonatomic, strong) HeaderView *headerView;
@property (nonatomic, strong) ClubFooterView *clubFooterView;

- (ProfileCell*)collectionViewCellForThing:(User *)user;
- (IBAction)checkinAction:(CbButton *)sender;
- (IBAction)directionAction:(id)sender;


@end
