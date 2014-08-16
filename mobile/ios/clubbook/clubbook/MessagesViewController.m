//
//  MessagesViewController.m
//  Clubbook
//
//  Created by Andrew on 7/12/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "MessagesViewController.h"
#import "MessageCell.h"
#import "UIImageView+WebCache.h"
#import "ChatViewController.h"
#import "GlobalVars.h"
#import "Constants.h"
#import "Conversation.h"
#import "Cloudinary.h"

@interface MessagesViewController (){
    NSArray *_chats;
}

@end

@implementation MessagesViewController

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
    // Do any additional setup after loading the view.
    self.messageTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

- (void)viewWillAppear:(BOOL)animated
{
    [PubNub setDelegate:self];
    [self showProgress:NO title:nil];
    [self loadChats];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message {
    PNLog(PNLogGeneralLevel, self, @"PubNub client received message: %@", message);
    [self loadChats];
}

- (void)loadChats
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [self._manager retrieveConversations:userId];
}

- (void)didReceiveConversations:(NSArray *)chats
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _chats = chats;
         
        self.messageTable.hidden = NO;
        self.messageTable.dataSource = self;
        self.messageTable.delegate = self;
        [self.messageTable reloadData];
    });
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _chats.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    MessageCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    Chat *chat = _chats[indexPath.row];
    
    cell.userName.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:17];
    [cell.userName setText:chat.receiver.name];
    
    cell.messageLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    Conversation *conv = [chat.conversations objectAtIndex:0];
    [cell.messageLabel setText:conv.msg];
    
    cell.unreadMsgCount.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:16];
    [cell.unreadMsgCount setText: [NSString stringWithFormat:@"%d", chat.unreadMessages]];
    cell.unreadMsgCount.hidden = (chat.unreadMessages == 0);
    
    //[cell.checkinButton setBackgroundColor:[UIColor colorWithRed:92/255.0 green:142/255.0 blue:95/255.0 alpha:1.0] forState:UIControlStateNormal];
    
    //[cell.checkinButton setBackgroundColor:[UIColor colorWithRed:115/255.0 green:178/255.0 blue:119/255.0 alpha:1.0] forState:UIControlStateHighlighted];
    
    // transform avatar
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLTransformation *transformation = [CLTransformation transformation];
    [transformation setParams: @{@"width": @100, @"height": @100, @"crop": @"thumb", @"gravity": @"face"}];
    
    NSString * avatarUrl  = [cloudinary url: [chat.receiver.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
    
    [cell.userAvatar setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"Default.png"]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.messageTable indexPathForSelectedRow];
    Chat *chat = _chats[selectedIndexPath.row];
    [self performSegueWithIdentifier: @"onChat" sender: chat];
    
   // [self performSegueWithIdentifier: @"onMessage" sender: place];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSString *)sender
{
    if([[segue identifier] isEqualToString:@"onChat"]){
        ChatViewController *chatController =  [segue destinationViewController];
        Chat *chat = (Chat*) sender;
        chatController.userTo = chat.receiver.id;
    }
}

@end
