//
//  Created by Jesse Squires
//  http://www.jessesquires.com
//
//
//  Documentation
//  http://cocoadocs.org/docsets/JSQMessagesViewController
//
//
//  GitHub
//  https://github.com/jessesquires/JSQMessagesViewController
//
//
//  License
//  Copyright (c) 2014 Jesse Squires
//  Released under an MIT license: http://opensource.org/licenses/MIT
//

#import "JSQMessagesCollectionViewCell.h"

#import "JSQMessagesCollectionViewCellIncoming.h"
#import "JSQMessagesCollectionViewCellOutgoing.h"
#import "JSQMessagesCollectionViewLayoutAttributes.h"

#import "UIView+JSQMessages.h"
#import "UIDevice+JSQMessages.h"


@implementation CustomActionMenuForMediaCell

-(id) initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _controlsView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 150, 60)];
        self.backgroundColor = [UIColor colorWithRed:0.106 green:0.055 blue:0.127 alpha:0.800];
        _controlsView.center = self.center;
        
        _menuItemLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 150, 30)];
        _yesButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 30, 75, 30)];
        _noButton = [[UIButton alloc] initWithFrame:CGRectMake(75, 30, 75, 30)];
        
        _menuItemLabel.text = @"Delete?";
        [_menuItemLabel setFont:[UIFont fontWithName:@"Arial-BoldMT" size:20]];
        _menuItemLabel.textColor = [UIColor whiteColor];
        _menuItemLabel.textAlignment = NSTextAlignmentCenter;
        
        [_yesButton setTitle:@"Yes" forState:UIControlStateNormal];
        [_yesButton.titleLabel setTextAlignment:NSTextAlignmentCenter];
        [_yesButton addTarget:self action:@selector(yesDelete) forControlEvents:UIControlEventTouchUpInside];
        
        [_noButton setTitle:@"No" forState:UIControlStateNormal];
        [_noButton.titleLabel setTextAlignment:NSTextAlignmentCenter];
        [_yesButton addTarget:self action:@selector(noDelete) forControlEvents:UIControlEventTouchUpInside];
        
        [_controlsView addSubview:_menuItemLabel];
        [_controlsView addSubview:_yesButton];
        [_controlsView addSubview:_noButton];
        [self addSubview:_controlsView];
        
        self.hidden = YES;
    }
    
    return self;
}

- (void) yesDelete {
    if (_deleteMenuIsShown) {
        [self showCustomMenu:NO];
        if  ([self.delegate respondsToSelector:@selector(didPressDelete)]){
            [self.delegate performSelector:@selector(didPressDelete)];
        }
    }
}

- (void) noDelete {
    if (_deleteMenuIsShown) {
        [self showCustomMenu:NO];
    }
}

- (void) showCustomMenuInFrame:(CGRect) frame {
    [self resetFrame:frame];
    [self showCustomMenu:YES];
}

- (void) resetFrame:(CGRect) frame {
    self.frame = frame;
    _controlsView.center = self.center;
}

- (void) showCustomMenu:(BOOL) show{
    _deleteMenuIsShown = show;
    self.hidden = !show;
}

- (void) touchedMenu:(CGPoint) point {
    CGRect yesFrame = [self.controlsView convertRect:self.yesButton.frame toView:self];
    CGRect noFrame = [self.controlsView convertRect:self.noButton.frame toView:self];
    
    if (CGRectContainsPoint(yesFrame, point)) {
        [self yesDelete];
    }
    else if (CGRectContainsPoint(noFrame, point)) {
        [self noDelete];
    }
}

@end

@interface JSQMessagesCollectionViewCell ()

@property (weak, nonatomic) IBOutlet JSQMessagesLabel *cellTopLabel;
@property (weak, nonatomic) IBOutlet JSQMessagesLabel *messageBubbleTopLabel;
@property (weak, nonatomic) IBOutlet JSQMessagesLabel *cellBottomLabel;

@property (weak, nonatomic) IBOutlet UIView *messageBubbleContainerView;
@property (weak, nonatomic) IBOutlet UIImageView *messageBubbleImageView;
@property (weak, nonatomic) IBOutlet JSQMessagesCellTextView *textView;

@property (weak, nonatomic) IBOutlet UIImageView *avatarImageView;
@property (weak, nonatomic) IBOutlet UIView *avatarContainerView;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *messageBubbleContainerWidthConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *textViewTopVerticalSpaceConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *textViewBottomVerticalSpaceConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *textViewAvatarHorizontalSpaceConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *textViewMarginHorizontalSpaceConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *cellTopLabelHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *messageBubbleTopLabelHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *cellBottomLabelHeightConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *avatarContainerViewWidthConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *avatarContainerViewHeightConstraint;

@property (assign, nonatomic) UIEdgeInsets textViewFrameInsets;

@property (assign, nonatomic) CGSize avatarViewSize;

@property (weak, nonatomic, readwrite) UITapGestureRecognizer *tapGestureRecognizer;
@property (weak, nonatomic, readwrite) UILongPressGestureRecognizer *longPressGestureRecognizer;

@property (strong, nonatomic) CustomActionMenuForMediaCell *customMenu;

- (void)jsq_handleTapGesture:(UITapGestureRecognizer *)tap;

- (void)jsq_handleLongPressGesture:(UILongPressGestureRecognizer *)press;

- (void)jsq_updateConstraint:(NSLayoutConstraint *)constraint withConstant:(CGFloat)constant;

@end



@implementation JSQMessagesCollectionViewCell

#pragma mark - Class methods

+ (UINib *)nib
{
    return [UINib nibWithNibName:NSStringFromClass([self class]) bundle:[NSBundle mainBundle]];
}

+ (NSString *)cellReuseIdentifier
{
    return NSStringFromClass([self class]);
}

+ (NSString *)mediaCellReuseIdentifier
{
    return [NSString stringWithFormat:@"%@_JSQMedia", NSStringFromClass([self class])];
}

#pragma mark - Initialization

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self setTranslatesAutoresizingMaskIntoConstraints:NO];
    
    self.backgroundColor = [UIColor whiteColor];
    
    self.cellTopLabelHeightConstraint.constant = 0.0f;
    self.messageBubbleTopLabelHeightConstraint.constant = 0.0f;
    self.cellBottomLabelHeightConstraint.constant = 0.0f;
    
    self.avatarViewSize = CGSizeZero;
    
    self.cellTopLabel.textAlignment = NSTextAlignmentCenter;
    self.cellTopLabel.font = [UIFont boldSystemFontOfSize:12.0f];
    self.cellTopLabel.textColor = [UIColor lightGrayColor];
    
    self.messageBubbleTopLabel.font = [UIFont systemFontOfSize:12.0f];
    self.messageBubbleTopLabel.textColor = [UIColor lightGrayColor];
    
    self.cellBottomLabel.font = [UIFont systemFontOfSize:11.0f];
    self.cellBottomLabel.textColor = [UIColor lightGrayColor];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(jsq_handleTapGesture:)];
    [self addGestureRecognizer:tap];
    self.tapGestureRecognizer = tap;
    
    UILongPressGestureRecognizer *press = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(jsq_handleLongPressGesture:)];
    [self addGestureRecognizer:press];
    self.longPressGestureRecognizer = press;
}

- (void)dealloc
{
    _delegate = nil;
    
    _cellTopLabel = nil;
    _messageBubbleTopLabel = nil;
    _cellBottomLabel = nil;
    
    _textView = nil;
    _messageBubbleImageView = nil;
    _mediaView = nil;
    
    _avatarImageView = nil;
    
    [_tapGestureRecognizer removeTarget:nil action:NULL];
    _tapGestureRecognizer = nil;
    
    [_longPressGestureRecognizer removeTarget:nil action:NULL];
    _longPressGestureRecognizer = nil;
}

#pragma mark - Collection view cell

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    self.cellTopLabel.text = nil;
    self.messageBubbleTopLabel.text = nil;
    self.cellBottomLabel.text = nil;
    
    self.textView.dataDetectorTypes = UIDataDetectorTypeNone;
    self.textView.text = nil;
    self.textView.attributedText = nil;
    
    self.avatarImageView.image = nil;
    self.avatarImageView.highlightedImage = nil;
}

- (void)applyLayoutAttributes:(UICollectionViewLayoutAttributes *)layoutAttributes
{
    [super applyLayoutAttributes:layoutAttributes];
    
    JSQMessagesCollectionViewLayoutAttributes *customAttributes = (JSQMessagesCollectionViewLayoutAttributes *)layoutAttributes;
    
    if (self.textView.font != customAttributes.messageBubbleFont) {
        self.textView.font = customAttributes.messageBubbleFont;
    }
    
    if (!UIEdgeInsetsEqualToEdgeInsets(self.textView.textContainerInset, customAttributes.textViewTextContainerInsets)) {
        self.textView.textContainerInset = customAttributes.textViewTextContainerInsets;
    }
    
    self.textViewFrameInsets = customAttributes.textViewFrameInsets;
    
    [self jsq_updateConstraint:self.messageBubbleContainerWidthConstraint
                  withConstant:customAttributes.messageBubbleContainerViewWidth];
    
    [self jsq_updateConstraint:self.cellTopLabelHeightConstraint
                  withConstant:customAttributes.cellTopLabelHeight];
    
    [self jsq_updateConstraint:self.messageBubbleTopLabelHeightConstraint
                  withConstant:customAttributes.messageBubbleTopLabelHeight];
    
    [self jsq_updateConstraint:self.cellBottomLabelHeightConstraint
                  withConstant:customAttributes.cellBottomLabelHeight];
    
    if ([self isKindOfClass:[JSQMessagesCollectionViewCellIncoming class]]) {
        self.avatarViewSize = customAttributes.incomingAvatarViewSize;
    }
    else if ([self isKindOfClass:[JSQMessagesCollectionViewCellOutgoing class]]) {
        self.avatarViewSize = customAttributes.outgoingAvatarViewSize;
    }
}

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    self.messageBubbleImageView.highlighted = highlighted;
}

- (void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
    self.messageBubbleImageView.highlighted = selected;
}

//  FIXME: radar 18326340
//         remove when fixed
//         hack for Xcode6 / iOS 8 SDK rendering bug that occurs on iOS 7.x
//         see issue #484
//         https://github.com/jessesquires/JSQMessagesViewController/issues/484
//
- (void)setBounds:(CGRect)bounds
{
    [super setBounds:bounds];
    
    if ([UIDevice jsq_isCurrentDeviceBeforeiOS8]) {
        self.contentView.frame = bounds;
    }
}

#pragma mark - Setters

- (void)setBackgroundColor:(UIColor *)backgroundColor
{
    [super setBackgroundColor:backgroundColor];
    
    self.cellTopLabel.backgroundColor = backgroundColor;
    self.messageBubbleTopLabel.backgroundColor = backgroundColor;
    self.cellBottomLabel.backgroundColor = backgroundColor;
    
    self.messageBubbleImageView.backgroundColor = backgroundColor;
    self.avatarImageView.backgroundColor = backgroundColor;
    
    self.messageBubbleContainerView.backgroundColor = backgroundColor;
    self.avatarContainerView.backgroundColor = backgroundColor;
}

- (void)setAvatarViewSize:(CGSize)avatarViewSize
{
    if (CGSizeEqualToSize(avatarViewSize, self.avatarViewSize)) {
        return;
    }
    
    [self jsq_updateConstraint:self.avatarContainerViewWidthConstraint withConstant:avatarViewSize.width];
    [self jsq_updateConstraint:self.avatarContainerViewHeightConstraint withConstant:avatarViewSize.height];
}

- (void)setTextViewFrameInsets:(UIEdgeInsets)textViewFrameInsets
{
    if (UIEdgeInsetsEqualToEdgeInsets(textViewFrameInsets, self.textViewFrameInsets)) {
        return;
    }
    
    [self jsq_updateConstraint:self.textViewTopVerticalSpaceConstraint withConstant:textViewFrameInsets.top];
    [self jsq_updateConstraint:self.textViewBottomVerticalSpaceConstraint withConstant:textViewFrameInsets.bottom];
    [self jsq_updateConstraint:self.textViewAvatarHorizontalSpaceConstraint withConstant:textViewFrameInsets.right];
    [self jsq_updateConstraint:self.textViewMarginHorizontalSpaceConstraint withConstant:textViewFrameInsets.left];
}

- (void)setMediaView:(UIView *)mediaView
{
    if ([_mediaView isEqual:mediaView]) {
        return;
    }
    
    [self.messageBubbleImageView removeFromSuperview];
    [self.textView removeFromSuperview];
    
    [mediaView setTranslatesAutoresizingMaskIntoConstraints:NO];
    mediaView.frame = self.messageBubbleContainerView.bounds;
    
    [self.messageBubbleContainerView addSubview:mediaView];
    [self.messageBubbleContainerView jsq_pinAllEdgesOfSubview:mediaView];
    _mediaView = mediaView;
    
    _customMenu = [[CustomActionMenuForMediaCell alloc] initWithFrame:_mediaView.frame];
    _customMenu.delegate = self;
    [_mediaView addSubview:_customMenu];
   
    //  because of cell re-use (and caching media views, if using built-in library media item)
    //  we may have dequeued a cell with a media view and add this one on top
    //  thus, remove any additional subviews hidden behind the new media view
    dispatch_async(dispatch_get_main_queue(), ^{
        for (NSUInteger i = 0; i < self.messageBubbleContainerView.subviews.count; i++) {
            if (self.messageBubbleContainerView.subviews[i] != _mediaView) {
                [self.messageBubbleContainerView.subviews[i] removeFromSuperview];
            }
        }
    });
}

#pragma mark - Getters

- (CGSize)avatarViewSize
{
    return CGSizeMake(self.avatarContainerViewWidthConstraint.constant,
                      self.avatarContainerViewHeightConstraint.constant);
}

- (UIEdgeInsets)textViewFrameInsets
{
    return UIEdgeInsetsMake(self.textViewTopVerticalSpaceConstraint.constant,
                            self.textViewMarginHorizontalSpaceConstraint.constant,
                            self.textViewBottomVerticalSpaceConstraint.constant,
                            self.textViewAvatarHorizontalSpaceConstraint.constant);
}

#pragma mark - Utilities

- (void)jsq_updateConstraint:(NSLayoutConstraint *)constraint withConstant:(CGFloat)constant
{
    if (constraint.constant == constant) {
        return;
    }
    
    constraint.constant = constant;
}

#pragma mark - Gesture recognizers

- (void)jsq_handleTapGesture:(UITapGestureRecognizer *)tap
{
    CGPoint touchPt = [tap locationInView:self];
    
    if (CGRectContainsPoint(self.avatarContainerView.frame, touchPt)) {
        if (_customMenu.deleteMenuIsShown) {
            [_customMenu showCustomMenu:NO];
        }
        [self.delegate messagesCollectionViewCellDidTapAvatar:self];
    }
    else if (CGRectContainsPoint(self.messageBubbleContainerView.frame, touchPt)) {
        if (!_customMenu.deleteMenuIsShown) {
            [self.delegate messagesCollectionViewCellDidTapMessageBubble:self];
        }
        else {
            CGPoint convertPoint = [tap locationInView:self.customMenu];
            [_customMenu touchedMenu:convertPoint];
        }
    }
    else {
        if (_customMenu.deleteMenuIsShown) {
            [_customMenu showCustomMenu:NO];
        }
        [self.delegate messagesCollectionViewCellDidTapCell:self atPosition:touchPt];
    }
}

- (void)jsq_handleLongPressGesture:(UILongPressGestureRecognizer *)press
{
    CGPoint touchPt = [press locationInView:self];
    
    if (CGRectContainsPoint(self.avatarContainerView.frame, touchPt)) {
        [self.delegate messagesCollectionViewCellDidLongPressAvatar:self];
    }
    else if (CGRectContainsPoint(self.messageBubbleContainerView.frame, touchPt)) {
        [self didLongPressOnBubble];
    }
    else {
        [self.delegate messagesCollectionViewCellDidLongPressCell:self atPosition:touchPt];
    }
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
{
    CGPoint touchPt = [touch locationInView:self];
    
    if ([gestureRecognizer isKindOfClass:[UILongPressGestureRecognizer class]]) {
        return CGRectContainsPoint(self.messageBubbleContainerView.frame, touchPt);
    }
    
    return YES;
}

-(BOOL) canPerformAction:(SEL)action withSender:(id)sender{
    if (action == @selector(delete:)) {
        return YES;
    }
    return NO;
}

-(void) delete:(id)sender {
    UICollectionView* collecitonView=(UICollectionView*)[self superview];
    if ([collecitonView isKindOfClass:[UICollectionView class]]) {
        id <UICollectionViewDelegate> d=collecitonView.delegate;
        if  ([d respondsToSelector:@selector(collectionView:performAction:forItemAtIndexPath:withSender:)]){
            [d collectionView:collecitonView performAction:@selector(delete:) forItemAtIndexPath:[collecitonView indexPathForCell:self] withSender:sender];
        }
    }
}
- (void) didPressDelete {
    [self delete:nil];
}

- (void) didLongPressOnBubble {
    if (_mediaView && _customMenu && !_customMenu.deleteMenuIsShown) {
        [_customMenu showCustomMenuInFrame:_mediaView.frame];
    }
}

- (void) cellDeselected {
    if (_customMenu && _customMenu.deleteMenuIsShown) {
        [_customMenu showCustomMenu:NO];
    }
}

@end
