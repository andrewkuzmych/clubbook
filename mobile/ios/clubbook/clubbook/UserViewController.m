//
//  UserViewController.m
//  Clubbook
//
//  Created by Andrew on 7/2/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "UserViewController.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "ChatViewController.h"

@interface UserViewController (){
    User *_user;
    float oldX;
}

@end

@implementation UserViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userId = [defaults objectForKey:@"userId"];
        [self._manager retrieveFriend:self.userId currnetUserId:userId];
        [self showProgress:YES title:nil];
    });

   // [self.addFriendButton setMainState:NSLocalizedString(@"Checkin", nil)];
    
    self.connectButton.layer.borderColor = [UIColor whiteColor].CGColor;
    
    self.nameLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:16.0];
    self.connectButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:14.0];
    self.ageLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:13.0];
    self.addFriendButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:14.0];
    self.countryLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:14.0];
    self.aboutMeTitleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:15.0];
    self.aboutMeLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12.0];
    
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didReceiveFriend:(User *)user
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
        
        for (int i = 0; i < [user.photos count]; i++) {
            CGRect frame;
            frame.origin.x = self.imageScrollView.frame.size.width * i;
            frame.origin.y = 0;
            frame.size = self.imageScrollView.frame.size;
            
            UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
            imageView.contentMode = UIViewContentModeScaleAspectFit;
            
            // transform avatar
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @450, @"height": @450,}];
            
            NSString *avatarUrl = [cloudinary url: [[user.photos objectAtIndex:i] valueForKey:@"public_id"] options:@{@"transformation": transformation}];

            [imageView setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"Default.png"]];
            [imageView setBackgroundColor:[UIColor blackColor]];
            [self.imageScrollView addSubview:imageView];
            
        }
        
        [self.addFriendButton setButtonState:user.friend_status];

        self.nameLabel.text = user.name;
        self.imageScrollView.delegate = self;
        self.countryLabel.text = user.country;
        if (user.bio != nil) {
            self.aboutMeLabel.text = user.bio;
        } else {
            self.aboutMeLabel.text = NSLocalizedString(@"defoutBio", nil);
        }
        NSString* userAge = user.age;
        NSString* userGender = user.gender;
        
        //defoutBio
        if (userAge != nil) {
            self.ageLabel.text = [NSString stringWithFormat:@"%@, %@",userAge, NSLocalizedString(userGender, nil)];
        }
        
        self.imagePageView.numberOfPages = self.imageScrollView.subviews.count;
        
        self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [user.photos count]  , self.imageScrollView.frame.size.height + 5);
            
        _user = user;
    });
}

- (void)didSendFriend:(User *)user
{
   [self hideProgress];
   [self.addFriendButton setButtonState:NSLocalizedString(@"sentRequest", nil)];
}

- (void)didConfirmFriend:(User *)user
{
    [self hideProgress];
    [self.addFriendButton setButtonState:NSLocalizedString(@"friend", nil)];
}

- (void)didRemoveFriend:(User *)user
{
    [self hideProgress];
    [self.addFriendButton setButtonState:NSLocalizedString(@"receiveRequest", nil)];
}


#pragma mark - UIScrollView Delegate
- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
    
    // Update the page when more than 50% of the previous/next page is visible
    CGFloat pageWidth = self.imageScrollView.frame.size.width;
    int page = floor((self.imageScrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.imagePageView.currentPage = page;
}


-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSIndexPath *)indexPath
{
    if([[segue identifier] isEqualToString:@"onChat"]){
        ChatViewController *chatController =  [segue destinationViewController];
        chatController.userTo = self.userId;
    }
}

- (IBAction)chatAction:(id)sender {
    [self performSegueWithIdentifier: @"onChat" sender: sender];
}

- (IBAction)addFriendAction:(AddFriendButton *)sender {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];

    if ([sender.friendState isEqualToString:NSLocalizedString(@"noneFriend", nil)]) {
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager sendFriendReguest:userId friendId:self.userId];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"receiveRequest", nil)]){
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager confirmFriendRequest:userId friendId:self.userId];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"friend", nil)]){
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager removeFriend:userId friendId:self.userId];
    }
}
@end
