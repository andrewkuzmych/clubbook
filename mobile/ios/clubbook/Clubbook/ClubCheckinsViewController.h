//
//  ClubUsersViewController.h
//  Clubbook
//
//  Created by Andrew on 7/28/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "User.h"
#import "ProfileCell.h"
#import "BaseViewController.h"
#import "FloatingButton.h"

@interface ClubCheckinsViewController : BaseViewController <UICollectionViewDelegate, UICollectionViewDataSource, UIAlertViewDelegate, PNDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *profileCollection;
@property (strong, nonatomic) NSString *placeId;
@property (strong, nonatomic) Place *place;
@property (assign, nonatomic) BOOL hasBack;
@property (weak, nonatomic) IBOutlet UILabel *openCloseLabel;
@property (weak, nonatomic) IBOutlet UILabel *hoursLabel;
@property (weak, nonatomic) IBOutlet UILabel *checkinCount;
@property (weak, nonatomic) IBOutlet UILabel *friendsCount;
@property (weak, nonatomic) IBOutlet UILabel *distanceLabel;
@property (weak, nonatomic) IBOutlet FloatingButton *checkinButton;

- (ProfileCell*)collectionViewCellForThing:(User *)user;
- (IBAction)checkinAction:(UIButton *)sender;


@end
